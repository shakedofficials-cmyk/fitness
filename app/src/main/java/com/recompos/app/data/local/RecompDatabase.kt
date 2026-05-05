package com.recompos.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserSettingsEntity::class,
        ProgramPhaseEntity::class,
        WorkoutTemplateEntity::class,
        ExerciseTemplateEntity::class,
        RestTaskEntity::class,
        WorkoutSessionEntity::class,
        ExerciseSessionEntity::class,
        SetLogEntity::class,
        BodyweightLogEntity::class,
        WaistLogEntity::class,
        NutritionLogEntity::class,
        SleepLogEntity::class,
        DigestionLogEntity::class,
        StepsLogEntity::class,
        CardioLogEntity::class,
        SupplementLogEntity::class,
        HabitLogEntity::class,
        ProgressPhotoEntity::class,
        CoachRecommendationEntity::class,
        KnowledgeArticleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RecompDatabase : RoomDatabase() {
    abstract fun dao(): RecompDao

    companion object {
        @Volatile private var instance: RecompDatabase? = null

        fun get(context: Context): RecompDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    RecompDatabase::class.java,
                    "recompos.db"
                ).build().also { instance = it }
            }
    }
}
