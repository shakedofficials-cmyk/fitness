package com.recompos.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecompDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertSettings(settings: UserSettingsEntity)
    @Query("SELECT * FROM UserSettingsEntity WHERE id = 1") fun observeSettings(): Flow<UserSettingsEntity?>
    @Query("SELECT * FROM UserSettingsEntity WHERE id = 1") suspend fun getSettings(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertPhases(items: List<ProgramPhaseEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertWorkouts(items: List<WorkoutTemplateEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertExercises(items: List<ExerciseTemplateEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertRestTasks(items: List<RestTaskEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertArticles(items: List<KnowledgeArticleEntity>)

    @Query("SELECT COUNT(*) FROM WorkoutTemplateEntity") suspend fun workoutTemplateCount(): Int
    @Query("SELECT * FROM ProgramPhaseEntity WHERE :week BETWEEN weekStart AND weekEnd LIMIT 1") fun observePhase(week: Int): Flow<ProgramPhaseEntity?>
    @Query("SELECT * FROM WorkoutTemplateEntity WHERE dayNumber = :day LIMIT 1") fun observeWorkout(day: Int): Flow<WorkoutTemplateEntity?>
    @Query("SELECT * FROM WorkoutTemplateEntity ORDER BY dayNumber") fun observeWorkouts(): Flow<List<WorkoutTemplateEntity>>
    @Query("SELECT * FROM ExerciseTemplateEntity WHERE workoutTemplateId = :workoutId ORDER BY orderIndex") fun observeExercises(workoutId: Int): Flow<List<ExerciseTemplateEntity>>
    @Query("SELECT * FROM ExerciseTemplateEntity ORDER BY workoutTemplateId, orderIndex") fun observeAllExercises(): Flow<List<ExerciseTemplateEntity>>
    @Query("SELECT * FROM ExerciseTemplateEntity WHERE id = :id") fun observeExercise(id: Int): Flow<ExerciseTemplateEntity?>
    @Query("SELECT * FROM RestTaskEntity WHERE workoutTemplateId = :workoutId ORDER BY orderIndex") fun observeRestTasks(workoutId: Int): Flow<List<RestTaskEntity>>
    @Query("SELECT * FROM KnowledgeArticleEntity ORDER BY id") fun observeArticles(): Flow<List<KnowledgeArticleEntity>>

    @Insert suspend fun insertWorkoutSession(session: WorkoutSessionEntity): Long
    @Insert suspend fun insertExerciseSessions(items: List<ExerciseSessionEntity>): List<Long>
    @Insert suspend fun insertSetLog(set: SetLogEntity): Long
    @Query("UPDATE WorkoutSessionEntity SET finishedAtMillis = :finishedAt, status = :status, notes = :notes WHERE id = :sessionId") suspend fun finishWorkout(sessionId: Long, finishedAt: Long, status: String, notes: String)
    @Query("UPDATE ExerciseSessionEntity SET status = :status, notes = :notes WHERE id = :id") suspend fun updateExerciseStatus(id: Long, status: String, notes: String)
    @Query("SELECT * FROM WorkoutSessionEntity ORDER BY startedAtMillis DESC") fun observeWorkoutHistory(): Flow<List<WorkoutSessionEntity>>
    @Query("SELECT * FROM ExerciseSessionEntity WHERE workoutSessionId = :sessionId ORDER BY orderIndex") fun observeExerciseSessions(sessionId: Long): Flow<List<ExerciseSessionEntity>>
    @Query("SELECT * FROM SetLogEntity WHERE exerciseSessionId = :exerciseSessionId ORDER BY setNumber, completedAtMillis") fun observeSets(exerciseSessionId: Long): Flow<List<SetLogEntity>>
    @Query("SELECT * FROM SetLogEntity ORDER BY completedAtMillis") fun observeAllSets(): Flow<List<SetLogEntity>>
    @Query("SELECT * FROM SetLogEntity WHERE exerciseSessionId IN (SELECT id FROM ExerciseSessionEntity WHERE exerciseTemplateId = :exerciseId) ORDER BY completedAtMillis") fun observeSetsForExercise(exerciseId: Int): Flow<List<SetLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertBodyweight(log: BodyweightLogEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertWaist(log: WaistLogEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertNutrition(log: NutritionLogEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertSleep(log: SleepLogEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertDigestion(log: DigestionLogEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertSteps(log: StepsLogEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertCardio(log: CardioLogEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertSupplement(log: SupplementLogEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertHabit(log: HabitLogEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertPhoto(log: ProgressPhotoEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertRecommendation(log: CoachRecommendationEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertBodyweights(logs: List<BodyweightLogEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertWaists(logs: List<WaistLogEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertNutritionLogs(logs: List<NutritionLogEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertSleepLogs(logs: List<SleepLogEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertDigestionLogs(logs: List<DigestionLogEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertStepsLogs(logs: List<StepsLogEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertCardioLogs(logs: List<CardioLogEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertSetLogs(logs: List<SetLogEntity>)

    @Query("SELECT * FROM BodyweightLogEntity ORDER BY dateEpochDay") fun observeBodyweights(): Flow<List<BodyweightLogEntity>>
    @Query("SELECT * FROM WaistLogEntity ORDER BY dateEpochDay") fun observeWaists(): Flow<List<WaistLogEntity>>
    @Query("SELECT * FROM NutritionLogEntity ORDER BY dateEpochDay DESC") fun observeNutrition(): Flow<List<NutritionLogEntity>>
    @Query("SELECT * FROM SleepLogEntity ORDER BY dateEpochDay") fun observeSleep(): Flow<List<SleepLogEntity>>
    @Query("SELECT * FROM DigestionLogEntity ORDER BY dateEpochDay") fun observeDigestion(): Flow<List<DigestionLogEntity>>
    @Query("SELECT * FROM StepsLogEntity ORDER BY dateEpochDay") fun observeSteps(): Flow<List<StepsLogEntity>>
    @Query("SELECT * FROM CardioLogEntity ORDER BY dateEpochDay") fun observeCardio(): Flow<List<CardioLogEntity>>
    @Query("SELECT * FROM SupplementLogEntity ORDER BY dateEpochDay DESC") fun observeSupplements(): Flow<List<SupplementLogEntity>>
    @Query("SELECT * FROM HabitLogEntity ORDER BY dateEpochDay DESC") fun observeHabits(): Flow<List<HabitLogEntity>>
    @Query("SELECT * FROM ProgressPhotoEntity ORDER BY dateEpochDay DESC") fun observePhotos(): Flow<List<ProgressPhotoEntity>>
    @Query("SELECT * FROM CoachRecommendationEntity ORDER BY dateEpochDay DESC") fun observeRecommendations(): Flow<List<CoachRecommendationEntity>>
}
