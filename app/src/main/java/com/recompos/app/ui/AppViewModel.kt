package com.recompos.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.recompos.app.RecompOsApplication
import com.recompos.app.data.local.ExerciseTemplateEntity
import com.recompos.app.data.local.SetLogEntity
import com.recompos.app.data.local.WorkoutTemplateEntity
import com.recompos.app.data.repository.AppPreferences
import com.recompos.app.data.repository.RecompRepository
import com.recompos.app.data.repository.SettingsStore
import com.recompos.app.domain.CoachReviewEngine
import com.recompos.app.domain.DeloadCalculator
import com.recompos.app.domain.ProgramCalendar
import com.recompos.app.export.ExportImportManager
import com.recompos.app.notifications.ReminderScheduler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DashboardState(
    val prefs: AppPreferences = AppPreferences(),
    val week: Int = 1,
    val day: Int = 1,
    val complete: Boolean = false,
    val phaseName: String = "Foundation + Logbook",
    val isDeload: Boolean = false,
    val workout: WorkoutTemplateEntity? = null,
    val coachTitle: String = "Recomp is on track",
    val coachMessage: String = "Log today, protect the shoulder, and beat the book cleanly."
)

private data class DashboardBase(
    val prefs: AppPreferences,
    val workouts: List<WorkoutTemplateEntity>,
    val bodyweights: List<com.recompos.app.data.local.BodyweightLogEntity>,
    val waists: List<com.recompos.app.data.local.WaistLogEntity>,
    val sleep: List<com.recompos.app.data.local.SleepLogEntity>
)

class AppViewModel(
    application: Application,
    private val repository: RecompRepository,
    private val settingsStore: SettingsStore,
    private val reminderScheduler: ReminderScheduler
) : AndroidViewModel(application) {
    private val calendar = ProgramCalendar()
    private val coach = CoachReviewEngine()
    private val exportImport = ExportImportManager()
    val deloadCalculator = DeloadCalculator()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    val preferences = settingsStore.preferences.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppPreferences())
    val workouts = repository.workouts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val allExercises = repository.allExercises().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val history = repository.history().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val bodyweights = repository.bodyweights().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val waists = repository.waists().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val nutrition = repository.nutrition().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val sleep = repository.sleep().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val digestion = repository.digestion().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val steps = repository.steps().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val cardio = repository.cardio().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val supplements = repository.supplements().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val habits = repository.habits().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val articles = repository.articles().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val sets = repository.allSets().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private val dashboardBase = combine(
        preferences,
        workouts,
        bodyweights,
        waists,
        sleep,
    ) { prefs, workoutList, bw, waist, sleepLogs ->
        DashboardBase(prefs, workoutList, bw, waist, sleepLogs)
    }

    val dashboard: StateFlow<DashboardState> = combine(
        dashboardBase,
        digestion,
        steps
    ) { base, digestionLogs, stepsLogs ->
        val prefs = base.prefs
        val date = calendar.current(prefs.startDate)
        val workout = base.workouts.firstOrNull { it.dayNumber == date.day }
        val signal = coach.review(base.bodyweights, base.waists, base.sleep, digestionLogs, stepsLogs)
        DashboardState(
            prefs = prefs,
            week = date.week,
            day = date.day,
            complete = date.isComplete,
            phaseName = when (date.week) {
                in 1..3 -> "Foundation + Logbook"
                4, 9 -> "Deload"
                in 5..8 -> "Specialization Push"
                else -> "Peak Recomp Block"
            },
            isDeload = date.week == 4 || date.week == 9,
            workout = workout,
            coachTitle = signal.title,
            coachMessage = signal.message
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardState())

    fun completeOnboarding(startDate: LocalDate, unitSystem: String, bodyweight: Double?, waist: Double?) {
        viewModelScope.launch {
            settingsStore.completeOnboarding(startDate, unitSystem)
            repository.ensureRoomSettings(startDate, unitSystem)
            if (bodyweight != null) repository.logBodyweight(bodyweight, "Baseline")
            if (waist != null) repository.logWaist(waist, "Baseline")
            reminderScheduler.scheduleDefaults()
        }
    }

    fun setTheme(mode: String) = viewModelScope.launch { settingsStore.updateTheme(mode) }
    fun setUnits(units: String) = viewModelScope.launch { settingsStore.updateUnits(units) }
    fun setReminders(enabled: Boolean) = viewModelScope.launch {
        settingsStore.updateReminders(enabled)
        if (enabled) reminderScheduler.scheduleDefaults() else reminderScheduler.cancelAll()
    }

    fun startWorkout(workout: WorkoutTemplateEntity, exercises: List<ExerciseTemplateEntity>, onStarted: (Long) -> Unit) {
        val state = dashboard.value
        viewModelScope.launch {
            val id = repository.startWorkout(LocalDate.now(), state.week, state.day, workout, exercises)
            onStarted(id)
        }
    }

    fun finishWorkout(sessionId: Long) = viewModelScope.launch {
        repository.finishWorkout(sessionId)
        _uiEvent.emit("Workout finished and saved.")
    }

    fun skipExercise(exerciseSessionId: Long, reason: String) = viewModelScope.launch {
        repository.skipExercise(exerciseSessionId, reason)
        _uiEvent.emit("Exercise skipped.")
    }

    fun deleteWorkoutSession(sessionId: Long) = viewModelScope.launch {
        repository.deleteWorkoutSession(sessionId)
        _uiEvent.emit("Workout session deleted.")
    }

    fun logSet(exerciseSessionId: Long, setNumber: Int, weight: Double, reps: Int, rir: Int, pain: Int, warmup: Boolean, drop: Boolean, notes: String) {
        viewModelScope.launch {
            repository.logSet(
                exerciseSessionId,
                SetLogEntity(
                    exerciseSessionId = exerciseSessionId,
                    setNumber = setNumber,
                    weight = weight,
                    reps = reps,
                    rir = rir,
                    painScore = pain,
                    isWarmup = warmup,
                    isDropSet = drop,
                    notes = notes,
                    completedAtMillis = System.currentTimeMillis()
                )
            )
            _uiEvent.emit("Set $setNumber saved.")
        }
    }

    fun deleteSet(id: Long) = viewModelScope.launch {
        repository.deleteSet(id)
        _uiEvent.emit("Set deleted.")
    }

    fun logBodyweight(value: Double) = viewModelScope.launch {
        repository.logBodyweight(value)
        _uiEvent.emit("Bodyweight saved.")
    }

    fun deleteBodyweight(id: Long) = viewModelScope.launch {
        repository.deleteBodyweight(id)
        _uiEvent.emit("Bodyweight log deleted.")
    }

    fun logWaist(value: Double) = viewModelScope.launch {
        repository.logWaist(value)
        _uiEvent.emit("Waist saved.")
    }

    fun deleteWaist(id: Long) = viewModelScope.launch {
        repository.deleteWaist(id)
        _uiEvent.emit("Waist log deleted.")
    }

    fun logNutrition(calories: Int, protein: Int, carbs: Int, fat: Int) = viewModelScope.launch {
        repository.logNutrition(calories, protein, carbs, fat)
        _uiEvent.emit("Nutrition saved.")
    }

    fun deleteNutrition(id: Long) = viewModelScope.launch {
        repository.deleteNutrition(id)
        _uiEvent.emit("Nutrition log deleted.")
    }

    fun logSleep(hours: Double, quality: Int) = viewModelScope.launch {
        repository.logSleep(hours, quality)
        _uiEvent.emit("Sleep saved.")
    }

    fun deleteSleep(id: Long) = viewModelScope.launch {
        repository.deleteSleep(id)
        _uiEvent.emit("Sleep log deleted.")
    }

    fun logDigestion(score: Int, reflux: Boolean, bloating: Boolean, triggers: String) = viewModelScope.launch {
        repository.logDigestion(score, reflux, bloating, triggers)
        _uiEvent.emit("Digestion saved.")
    }

    fun deleteDigestion(id: Long) = viewModelScope.launch {
        repository.deleteDigestion(id)
        _uiEvent.emit("Digestion log deleted.")
    }

    fun logSteps(steps: Int) = viewModelScope.launch {
        repository.logSteps(steps)
        _uiEvent.emit("Steps saved.")
    }

    fun deleteSteps(id: Long) = viewModelScope.launch {
        repository.deleteSteps(id)
        _uiEvent.emit("Steps log deleted.")
    }

    fun logCardio(modality: String, duration: Int, intensity: String) = viewModelScope.launch {
        repository.logCardio(modality, duration, intensity)
        _uiEvent.emit("Cardio saved.")
    }

    fun deleteCardio(id: Long) = viewModelScope.launch {
        repository.deleteCardio(id)
        _uiEvent.emit("Cardio log deleted.")
    }

    fun logSupplement(creatine: Boolean, whey: Boolean, vitaminD: Boolean, omega3: Boolean) = viewModelScope.launch {
        repository.logSupplement(creatine, whey, vitaminD, omega3)
        _uiEvent.emit("Supplements saved.")
    }

    fun deleteSupplement(id: Long) = viewModelScope.launch {
        repository.deleteSupplement(id)
        _uiEvent.emit("Supplement log deleted.")
    }

    fun logHabit(mobility: Boolean, breathing: Boolean, photo: Boolean) = viewModelScope.launch {
        repository.logHabit(mobility, breathing, photo)
        _uiEvent.emit("Habits saved.")
    }

    fun deleteHabit(id: Long) = viewModelScope.launch {
        repository.deleteHabit(id)
        _uiEvent.emit("Habit log deleted.")
    }

    fun logPhoto(viewType: String, uri: String) = viewModelScope.launch {
        repository.logPhoto(viewType, uri)
        _uiEvent.emit("Photo URI saved.")
    }

    fun deletePhoto(id: Long) = viewModelScope.launch {
        repository.deletePhoto(id)
        _uiEvent.emit("Photo log deleted.")
    }

    fun buildJsonExport(): String = exportImport.exportJson(
        bodyweights.value,
        waists.value,
        nutrition.value,
        sleep.value,
        digestion.value,
        steps.value,
        cardio.value,
        sets.value
    )

    fun buildSetCsv(): String = exportImport.exportCsv(
        "RecompOS set logs",
        listOf(listOf("exerciseSessionId", "set", "weight", "reps", "rir", "pain", "warmup", "drop", "completedAt")) +
            sets.value.map {
                listOf(
                    it.exerciseSessionId.toString(),
                    it.setNumber.toString(),
                    it.weight.toString(),
                    it.reps.toString(),
                    it.rir.toString(),
                    it.painScore.toString(),
                    it.isWarmup.toString(),
                    it.isDropSet.toString(),
                    it.completedAtMillis.toString()
                )
            }
    )

    fun buildFullCsv(): String = exportImport.exportCsv(
        "RecompOS logs",
        listOf(listOf("type", "dateOrTime", "value1", "value2", "value3", "value4", "notes")) +
            bodyweights.value.map { listOf("bodyweight", it.dateEpochDay.toString(), it.weight.toString(), "", "", "", it.notes) } +
            waists.value.map { listOf("waist", it.dateEpochDay.toString(), it.waist.toString(), "", "", "", it.notes) } +
            nutrition.value.map { listOf("nutrition", it.dateEpochDay.toString(), it.calories.toString(), it.protein.toString(), it.carbs.toString(), it.fat.toString(), it.notes) } +
            sleep.value.map { listOf("sleep", it.dateEpochDay.toString(), it.hours.toString(), it.quality.toString(), "", "", it.notes) } +
            digestion.value.map { listOf("digestion", it.dateEpochDay.toString(), it.score.toString(), it.reflux.toString(), it.bloating.toString(), it.triggerFoods, it.notes) } +
            steps.value.map { listOf("steps", it.dateEpochDay.toString(), it.steps.toString(), "", "", "", it.notes) } +
            cardio.value.map { listOf("cardio", it.dateEpochDay.toString(), it.modality, it.durationMinutes.toString(), it.intensity, "", it.notes) } +
            sets.value.map { listOf("set", it.completedAtMillis.toString(), it.exerciseSessionId.toString(), it.setNumber.toString(), "${it.weight}x${it.reps}", "RIR ${it.rir} pain ${it.painScore}", it.notes) }
    )

    fun validateImport(json: String): Boolean = exportImport.validateImport(json)
    fun importJson(json: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val snapshot = runCatching { exportImport.parseImport(json) }.getOrNull()
            if (snapshot == null) {
                onResult(false)
            } else {
                repository.importSnapshot(snapshot)
                onResult(true)
            }
        }
    }

    fun exerciseSessions(sessionId: Long) = repository.exerciseSessions(sessionId)
    fun exerciseSets(exerciseSessionId: Long) = repository.sets(exerciseSessionId)
    fun lastExerciseSets(exerciseTemplateId: Int, currentWorkoutSessionId: Long) = repository.lastExerciseSets(exerciseTemplateId, currentWorkoutSessionId)
    fun restTasks(workoutTemplateId: Int) = repository.restTasks(workoutTemplateId)
}

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val app = application as RecompOsApplication
        @Suppress("UNCHECKED_CAST")
        return AppViewModel(application, app.container.repository, app.container.settingsStore, app.container.reminderScheduler) as T
    }
}
