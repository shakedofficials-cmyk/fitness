package com.recompos.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val startDateEpochDay: Long,
    val unitSystem: String = "metric",
    val bodyweightUnit: String = "kg",
    val lengthUnit: String = "cm",
    val themeMode: String = "dark",
    val reminderPreferencesJson: String = "{}"
)

@Entity
data class ProgramPhaseEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val weekStart: Int,
    val weekEnd: Int,
    val description: String,
    val defaultRir: String,
    val deloadFlag: Boolean
)

@Entity
data class WorkoutTemplateEntity(
    @PrimaryKey val id: Int,
    val dayNumber: Int,
    val name: String,
    val description: String,
    val estimatedMinutes: Int,
    val isRestDay: Boolean
)

@Entity
data class ExerciseTemplateEntity(
    @PrimaryKey val id: Int,
    val workoutTemplateId: Int,
    val orderIndex: Int,
    val name: String,
    val targetMuscle: String,
    val secondaryMuscles: String,
    val sets: Int,
    val minReps: Int,
    val maxReps: Int,
    val restSecondsMin: Int,
    val restSecondsMax: Int,
    val rirTarget: String,
    val cues: String,
    val cautions: String,
    val alternatives: String,
    val dropSetAllowed: Boolean,
    val failureAllowed: Boolean,
    val progressionType: String = "double_progression"
)

@Entity
data class RestTaskEntity(
    @PrimaryKey val id: Int,
    val workoutTemplateId: Int,
    val orderIndex: Int,
    val title: String,
    val description: String
)

@Entity
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val weekNumber: Int,
    val dayNumber: Int,
    val workoutTemplateId: Int,
    val startedAtMillis: Long,
    val finishedAtMillis: Long?,
    val status: String,
    val notes: String = ""
)

@Entity
data class ExerciseSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutSessionId: Long,
    val exerciseTemplateId: Int,
    val orderIndex: Int,
    val status: String = "planned",
    val notes: String = "",
    val swappedFromExerciseId: Int? = null
)

@Entity
data class SetLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseSessionId: Long,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val rir: Int,
    val painScore: Int,
    val isWarmup: Boolean,
    val isDropSet: Boolean,
    val notes: String = "",
    val completedAtMillis: Long
)

@Entity
data class BodyweightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val weight: Double,
    val notes: String = ""
)

@Entity
data class WaistLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val waist: Double,
    val notes: String = ""
)

@Entity
data class NutritionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val waterMl: Int? = null,
    val notes: String = ""
)

@Entity
data class SleepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val hours: Double,
    val quality: Int,
    val notes: String = ""
)

@Entity
data class DigestionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val score: Int,
    val reflux: Boolean,
    val bloating: Boolean,
    val triggerFoods: String = "",
    val notes: String = ""
)

@Entity
data class StepsLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val steps: Int,
    val notes: String = ""
)

@Entity
data class CardioLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val modality: String,
    val durationMinutes: Int,
    val intensity: String,
    val notes: String = ""
)

@Entity
data class SupplementLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val creatineTaken: Boolean,
    val wheyTaken: Boolean,
    val vitaminDTaken: Boolean,
    val omega3Taken: Boolean,
    val notes: String = ""
)

@Entity
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val shoulderMobilityDone: Boolean,
    val breathingDone: Boolean,
    val progressPhotoDone: Boolean,
    val notes: String = ""
)

@Entity
data class ProgressPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val viewType: String,
    val uri: String,
    val notes: String = ""
)

@Entity
data class CoachRecommendationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val type: String,
    val title: String,
    val message: String,
    val severity: String
)

@Entity
data class KnowledgeArticleEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val body: String
)
