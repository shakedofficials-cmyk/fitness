package com.recompos.app.data.repository

import com.recompos.app.data.local.*
import com.recompos.app.export.ImportSnapshot
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class WorkoutSessionSnapshot(
    val session: WorkoutSessionEntity,
    val exercises: List<ExerciseSessionEntity>,
    val sets: List<SetLogEntity>
)

class RecompRepository(private val dao: RecompDao) {
    suspend fun seedIfNeeded() {
        if (dao.workoutTemplateCount() == 0) {
            dao.insertPhases(SeedData.phases)
            dao.insertWorkouts(SeedData.workouts)
            dao.insertExercises(SeedData.exercises)
            dao.insertRestTasks(SeedData.restTasks)
            dao.insertArticles(SeedData.articles)
        }
    }

    suspend fun ensureRoomSettings(startDate: LocalDate, unitSystem: String) {
        if (dao.getSettings() == null) {
            dao.upsertSettings(
                UserSettingsEntity(
                    startDateEpochDay = startDate.toEpochDay(),
                    unitSystem = unitSystem,
                    bodyweightUnit = if (unitSystem == "metric") "kg" else "lb",
                    lengthUnit = if (unitSystem == "metric") "cm" else "in"
                )
            )
        }
    }

    fun phase(week: Int) = dao.observePhase(week)
    fun workout(day: Int) = dao.observeWorkout(day)
    fun workouts() = dao.observeWorkouts()
    fun exercises(workoutId: Int) = dao.observeExercises(workoutId)
    fun allExercises() = dao.observeAllExercises()
    fun exercise(id: Int) = dao.observeExercise(id)
    fun restTasks(workoutId: Int) = dao.observeRestTasks(workoutId)
    fun articles() = dao.observeArticles()
    fun history() = dao.observeWorkoutHistory()
    fun exerciseSessions(sessionId: Long) = dao.observeExerciseSessions(sessionId)
    fun sets(exerciseSessionId: Long) = dao.observeSets(exerciseSessionId)
    fun lastExerciseSets(exerciseTemplateId: Int, currentWorkoutSessionId: Long) = dao.observeLastExerciseSets(exerciseTemplateId, currentWorkoutSessionId)
    fun allSets() = dao.observeAllSets()
    fun setsForExercise(exerciseId: Int) = dao.observeSetsForExercise(exerciseId)
    fun bodyweights() = dao.observeBodyweights()
    fun waists() = dao.observeWaists()
    fun nutrition() = dao.observeNutrition()
    fun sleep() = dao.observeSleep()
    fun digestion() = dao.observeDigestion()
    fun steps() = dao.observeSteps()
    fun cardio() = dao.observeCardio()
    fun supplements() = dao.observeSupplements()
    fun habits() = dao.observeHabits()
    fun photos() = dao.observePhotos()
    fun recommendations() = dao.observeRecommendations()

    suspend fun startWorkout(date: LocalDate, week: Int, day: Int, workout: WorkoutTemplateEntity, exercises: List<ExerciseTemplateEntity>): Long {
        val sessionId = dao.insertWorkoutSession(
            WorkoutSessionEntity(
                dateEpochDay = date.toEpochDay(),
                weekNumber = week,
                dayNumber = day,
                workoutTemplateId = workout.id,
                startedAtMillis = System.currentTimeMillis(),
                finishedAtMillis = null,
                status = "in_progress"
            )
        )
        dao.insertExerciseSessions(exercises.map { ExerciseSessionEntity(workoutSessionId = sessionId, exerciseTemplateId = it.id, orderIndex = it.orderIndex) })
        return sessionId
    }

    suspend fun logSet(exerciseSessionId: Long, set: SetLogEntity) = dao.insertSetLog(set.copy(exerciseSessionId = exerciseSessionId))
    suspend fun restoreSet(set: SetLogEntity) = dao.insertSetLog(set)
    suspend fun deleteSet(id: Long) = dao.deleteSetLog(id)
    suspend fun finishWorkout(sessionId: Long, notes: String = "") = dao.finishWorkout(sessionId, System.currentTimeMillis(), "completed", notes)
    suspend fun skipExercise(id: Long, reason: String) = dao.updateExerciseStatus(id, "skipped", reason)
    suspend fun snapshotWorkoutSession(id: Long): WorkoutSessionSnapshot? {
        val session = dao.getWorkoutSession(id) ?: return null
        return WorkoutSessionSnapshot(
            session = session,
            exercises = dao.getExerciseSessionsForWorkout(id),
            sets = dao.getSetLogsForWorkout(id)
        )
    }

    suspend fun deleteWorkoutSession(id: Long) = dao.deleteWorkoutCascade(id)

    suspend fun restoreWorkoutSession(snapshot: WorkoutSessionSnapshot) {
        dao.insertWorkoutSessions(listOf(snapshot.session))
        dao.insertExerciseSessionsReplace(snapshot.exercises)
        dao.insertSetLogs(snapshot.sets)
    }

    suspend fun logBodyweight(value: Double, notes: String = "") = dao.insertBodyweight(BodyweightLogEntity(dateEpochDay = LocalDate.now().toEpochDay(), weight = value, notes = notes))
    suspend fun restoreBodyweight(log: BodyweightLogEntity) = dao.insertBodyweight(log)
    suspend fun deleteBodyweight(id: Long) = dao.deleteBodyweightLog(id)
    suspend fun logWaist(value: Double, notes: String = "") = dao.insertWaist(WaistLogEntity(dateEpochDay = LocalDate.now().toEpochDay(), waist = value, notes = notes))
    suspend fun restoreWaist(log: WaistLogEntity) = dao.insertWaist(log)
    suspend fun deleteWaist(id: Long) = dao.deleteWaistLog(id)
    suspend fun logNutrition(calories: Int, protein: Int, carbs: Int, fat: Int, notes: String = "") = dao.insertNutrition(NutritionLogEntity(dateEpochDay = LocalDate.now().toEpochDay(), calories = calories, protein = protein, carbs = carbs, fat = fat, notes = notes))
    suspend fun restoreNutrition(log: NutritionLogEntity) = dao.insertNutrition(log)
    suspend fun deleteNutrition(id: Long) = dao.deleteNutritionLog(id)
    suspend fun logSleep(hours: Double, quality: Int, notes: String = "") = dao.insertSleep(SleepLogEntity(dateEpochDay = LocalDate.now().toEpochDay(), hours = hours, quality = quality, notes = notes))
    suspend fun restoreSleep(log: SleepLogEntity) = dao.insertSleep(log)
    suspend fun deleteSleep(id: Long) = dao.deleteSleepLog(id)
    suspend fun logDigestion(score: Int, reflux: Boolean, bloating: Boolean, triggers: String, notes: String = "") = dao.insertDigestion(DigestionLogEntity(dateEpochDay = LocalDate.now().toEpochDay(), score = score, reflux = reflux, bloating = bloating, triggerFoods = triggers, notes = notes))
    suspend fun restoreDigestion(log: DigestionLogEntity) = dao.insertDigestion(log)
    suspend fun deleteDigestion(id: Long) = dao.deleteDigestionLog(id)
    suspend fun logSteps(steps: Int, notes: String = "") = dao.insertSteps(StepsLogEntity(dateEpochDay = LocalDate.now().toEpochDay(), steps = steps, notes = notes))
    suspend fun restoreSteps(log: StepsLogEntity) = dao.insertSteps(log)
    suspend fun deleteSteps(id: Long) = dao.deleteStepsLog(id)
    suspend fun logCardio(modality: String, duration: Int, intensity: String, notes: String = "") = dao.insertCardio(CardioLogEntity(dateEpochDay = LocalDate.now().toEpochDay(), modality = modality, durationMinutes = duration, intensity = intensity, notes = notes))
    suspend fun restoreCardio(log: CardioLogEntity) = dao.insertCardio(log)
    suspend fun deleteCardio(id: Long) = dao.deleteCardioLog(id)
    suspend fun logSupplement(creatine: Boolean, whey: Boolean, vitaminD: Boolean, omega3: Boolean, notes: String = "") = dao.insertSupplement(SupplementLogEntity(dateEpochDay = LocalDate.now().toEpochDay(), creatineTaken = creatine, wheyTaken = whey, vitaminDTaken = vitaminD, omega3Taken = omega3, notes = notes))
    suspend fun restoreSupplement(log: SupplementLogEntity) = dao.insertSupplement(log)
    suspend fun deleteSupplement(id: Long) = dao.deleteSupplementLog(id)
    suspend fun logHabit(mobility: Boolean, breathing: Boolean, photo: Boolean, notes: String = "") = dao.insertHabit(HabitLogEntity(dateEpochDay = LocalDate.now().toEpochDay(), shoulderMobilityDone = mobility, breathingDone = breathing, progressPhotoDone = photo, notes = notes))
    suspend fun restoreHabit(log: HabitLogEntity) = dao.insertHabit(log)
    suspend fun deleteHabit(id: Long) = dao.deleteHabitLog(id)
    suspend fun logPhoto(viewType: String, uri: String, notes: String = "") = dao.insertPhoto(ProgressPhotoEntity(dateEpochDay = LocalDate.now().toEpochDay(), viewType = viewType, uri = uri, notes = notes))
    suspend fun restorePhoto(log: ProgressPhotoEntity) = dao.insertPhoto(log)
    suspend fun deletePhoto(id: Long) = dao.deletePhotoLog(id)
    suspend fun saveRecommendation(signal: com.recompos.app.domain.CoachSignal) = dao.insertRecommendation(CoachRecommendationEntity(dateEpochDay = LocalDate.now().toEpochDay(), type = signal.severity, title = signal.title, message = signal.message, severity = signal.severity))
    suspend fun clearUserData() = dao.clearUserData()

    suspend fun importSnapshot(snapshot: ImportSnapshot) {
        dao.insertBodyweights(snapshot.bodyweights)
        dao.insertWaists(snapshot.waists)
        dao.insertNutritionLogs(snapshot.nutrition)
        dao.insertSleepLogs(snapshot.sleep)
        dao.insertDigestionLogs(snapshot.digestion)
        dao.insertStepsLogs(snapshot.steps)
        dao.insertCardioLogs(snapshot.cardio)
        dao.insertSupplementLogs(snapshot.supplements)
        dao.insertHabitLogs(snapshot.habits)
        dao.insertPhotoLogs(snapshot.photos)
        dao.insertSetLogs(snapshot.sets)
    }
}
