package com.example.fitnessapp.`data`.local.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.example.fitnessapp.`data`.local.dao.ExerciseDao
import com.example.fitnessapp.`data`.local.dao.ExerciseDao_Impl
import com.example.fitnessapp.`data`.local.dao.FoodLogDao
import com.example.fitnessapp.`data`.local.dao.FoodLogDao_Impl
import com.example.fitnessapp.`data`.local.dao.SetDao
import com.example.fitnessapp.`data`.local.dao.SetDao_Impl
import com.example.fitnessapp.`data`.local.dao.UserProfileDao
import com.example.fitnessapp.`data`.local.dao.UserProfileDao_Impl
import com.example.fitnessapp.`data`.local.dao.WorkoutDao
import com.example.fitnessapp.`data`.local.dao.WorkoutDao_Impl
import com.example.fitnessapp.`data`.local.dao.WorkoutExerciseDao
import com.example.fitnessapp.`data`.local.dao.WorkoutExerciseDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Any
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _workoutDao: Lazy<WorkoutDao> = lazy {
    WorkoutDao_Impl(this)
  }


  private val _exerciseDao: Lazy<ExerciseDao> = lazy {
    ExerciseDao_Impl(this)
  }


  private val _workoutExerciseDao: Lazy<WorkoutExerciseDao> = lazy {
    WorkoutExerciseDao_Impl(this)
  }


  private val _setDao: Lazy<SetDao> = lazy {
    SetDao_Impl(this)
  }


  private val _userProfileDao: Lazy<UserProfileDao> = lazy {
    UserProfileDao_Impl(this)
  }


  private val _foodLogDao: Lazy<FoodLogDao> = lazy {
    FoodLogDao_Impl(this)
  }


  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(7,
        "df8c3cde4a1d3136c9c115d2e0942ac6", "95e369542cc232cc65309fa63cd3fad2") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `workouts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `startedAt` INTEGER NOT NULL, `finishedAt` INTEGER, `notes` TEXT, `manualDurationMinutes` INTEGER, `durationOffsetMs` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `exercises` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `muscleGroup` TEXT)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `workout_exercises` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `workoutId` INTEGER NOT NULL, `exerciseId` INTEGER NOT NULL, `orderIndex` INTEGER NOT NULL, `isDropset` INTEGER NOT NULL, `startingWeight` REAL NOT NULL, `dropWeightDecrease` REAL NOT NULL, FOREIGN KEY(`workoutId`) REFERENCES `workouts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`exerciseId`) REFERENCES `exercises`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_exercises_workoutId` ON `workout_exercises` (`workoutId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_exercises_exerciseId` ON `workout_exercises` (`exerciseId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `sets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `workoutExerciseId` INTEGER NOT NULL, `setNumber` INTEGER NOT NULL, `reps` INTEGER NOT NULL, `weight` REAL NOT NULL, `rpe` INTEGER, `isDrop` INTEGER NOT NULL, `completedAt` INTEGER, `completed` INTEGER NOT NULL, FOREIGN KEY(`workoutExerciseId`) REFERENCES `workout_exercises`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_sets_workoutExerciseId` ON `sets` (`workoutExerciseId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `user_profile` (`id` INTEGER NOT NULL, `age` INTEGER NOT NULL, `gender` TEXT NOT NULL, `height` REAL NOT NULL, `currentWeight` REAL NOT NULL, `targetWeight` REAL NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `food_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `carbs` REAL NOT NULL, `fats` REAL NOT NULL, `protein` REAL NOT NULL, `calories` REAL NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'df8c3cde4a1d3136c9c115d2e0942ac6')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `workouts`")
        connection.execSQL("DROP TABLE IF EXISTS `exercises`")
        connection.execSQL("DROP TABLE IF EXISTS `workout_exercises`")
        connection.execSQL("DROP TABLE IF EXISTS `sets`")
        connection.execSQL("DROP TABLE IF EXISTS `user_profile`")
        connection.execSQL("DROP TABLE IF EXISTS `food_logs`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsWorkouts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWorkouts.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkouts.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkouts.put("startedAt", TableInfo.Column("startedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkouts.put("finishedAt", TableInfo.Column("finishedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkouts.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkouts.put("manualDurationMinutes", TableInfo.Column("manualDurationMinutes",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkouts.put("durationOffsetMs", TableInfo.Column("durationOffsetMs", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWorkouts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWorkouts: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWorkouts: TableInfo = TableInfo("workouts", _columnsWorkouts, _foreignKeysWorkouts,
            _indicesWorkouts)
        val _existingWorkouts: TableInfo = read(connection, "workouts")
        if (!_infoWorkouts.equals(_existingWorkouts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |workouts(com.example.fitnessapp.data.local.entity.WorkoutEntity).
              | Expected:
              |""".trimMargin() + _infoWorkouts + """
              |
              | Found:
              |""".trimMargin() + _existingWorkouts)
        }
        val _columnsExercises: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsExercises.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("muscleGroup", TableInfo.Column("muscleGroup", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExercises: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesExercises: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoExercises: TableInfo = TableInfo("exercises", _columnsExercises,
            _foreignKeysExercises, _indicesExercises)
        val _existingExercises: TableInfo = read(connection, "exercises")
        if (!_infoExercises.equals(_existingExercises)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |exercises(com.example.fitnessapp.data.local.entity.ExerciseEntity).
              | Expected:
              |""".trimMargin() + _infoExercises + """
              |
              | Found:
              |""".trimMargin() + _existingExercises)
        }
        val _columnsWorkoutExercises: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWorkoutExercises.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutExercises.put("workoutId", TableInfo.Column("workoutId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutExercises.put("exerciseId", TableInfo.Column("exerciseId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutExercises.put("orderIndex", TableInfo.Column("orderIndex", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutExercises.put("isDropset", TableInfo.Column("isDropset", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutExercises.put("startingWeight", TableInfo.Column("startingWeight", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutExercises.put("dropWeightDecrease", TableInfo.Column("dropWeightDecrease",
            "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWorkoutExercises: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysWorkoutExercises.add(TableInfo.ForeignKey("workouts", "CASCADE", "NO ACTION",
            listOf("workoutId"), listOf("id")))
        _foreignKeysWorkoutExercises.add(TableInfo.ForeignKey("exercises", "CASCADE", "NO ACTION",
            listOf("exerciseId"), listOf("id")))
        val _indicesWorkoutExercises: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesWorkoutExercises.add(TableInfo.Index("index_workout_exercises_workoutId", false,
            listOf("workoutId"), listOf("ASC")))
        _indicesWorkoutExercises.add(TableInfo.Index("index_workout_exercises_exerciseId", false,
            listOf("exerciseId"), listOf("ASC")))
        val _infoWorkoutExercises: TableInfo = TableInfo("workout_exercises",
            _columnsWorkoutExercises, _foreignKeysWorkoutExercises, _indicesWorkoutExercises)
        val _existingWorkoutExercises: TableInfo = read(connection, "workout_exercises")
        if (!_infoWorkoutExercises.equals(_existingWorkoutExercises)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |workout_exercises(com.example.fitnessapp.data.local.entity.WorkoutExerciseEntity).
              | Expected:
              |""".trimMargin() + _infoWorkoutExercises + """
              |
              | Found:
              |""".trimMargin() + _existingWorkoutExercises)
        }
        val _columnsSets: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSets.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSets.put("workoutExerciseId", TableInfo.Column("workoutExerciseId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSets.put("setNumber", TableInfo.Column("setNumber", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSets.put("reps", TableInfo.Column("reps", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSets.put("weight", TableInfo.Column("weight", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSets.put("rpe", TableInfo.Column("rpe", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSets.put("isDrop", TableInfo.Column("isDrop", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSets.put("completedAt", TableInfo.Column("completedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSets.put("completed", TableInfo.Column("completed", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSets: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysSets.add(TableInfo.ForeignKey("workout_exercises", "CASCADE", "NO ACTION",
            listOf("workoutExerciseId"), listOf("id")))
        val _indicesSets: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSets.add(TableInfo.Index("index_sets_workoutExerciseId", false,
            listOf("workoutExerciseId"), listOf("ASC")))
        val _infoSets: TableInfo = TableInfo("sets", _columnsSets, _foreignKeysSets, _indicesSets)
        val _existingSets: TableInfo = read(connection, "sets")
        if (!_infoSets.equals(_existingSets)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |sets(com.example.fitnessapp.data.local.entity.SetEntity).
              | Expected:
              |""".trimMargin() + _infoSets + """
              |
              | Found:
              |""".trimMargin() + _existingSets)
        }
        val _columnsUserProfile: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUserProfile.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("age", TableInfo.Column("age", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("gender", TableInfo.Column("gender", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("height", TableInfo.Column("height", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("currentWeight", TableInfo.Column("currentWeight", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("targetWeight", TableInfo.Column("targetWeight", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUserProfile: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUserProfile: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUserProfile: TableInfo = TableInfo("user_profile", _columnsUserProfile,
            _foreignKeysUserProfile, _indicesUserProfile)
        val _existingUserProfile: TableInfo = read(connection, "user_profile")
        if (!_infoUserProfile.equals(_existingUserProfile)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |user_profile(com.example.fitnessapp.data.local.entity.UserProfileEntity).
              | Expected:
              |""".trimMargin() + _infoUserProfile + """
              |
              | Found:
              |""".trimMargin() + _existingUserProfile)
        }
        val _columnsFoodLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFoodLogs.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("date", TableInfo.Column("date", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("carbs", TableInfo.Column("carbs", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("fats", TableInfo.Column("fats", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("protein", TableInfo.Column("protein", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("calories", TableInfo.Column("calories", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFoodLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFoodLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFoodLogs: TableInfo = TableInfo("food_logs", _columnsFoodLogs,
            _foreignKeysFoodLogs, _indicesFoodLogs)
        val _existingFoodLogs: TableInfo = read(connection, "food_logs")
        if (!_infoFoodLogs.equals(_existingFoodLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |food_logs(com.example.fitnessapp.data.local.entity.FoodLogEntity).
              | Expected:
              |""".trimMargin() + _infoFoodLogs + """
              |
              | Found:
              |""".trimMargin() + _existingFoodLogs)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "workouts", "exercises",
        "workout_exercises", "sets", "user_profile", "food_logs")
  }

  public override fun clearAllTables() {
    super.performClear(true, "workouts", "exercises", "workout_exercises", "sets", "user_profile",
        "food_logs")
  }

  protected override fun getRequiredTypeConverterClasses():
      Map<KClass<out Any>, List<KClass<out Any>>> {
    val _typeConvertersMap: MutableMap<KClass<out Any>, List<KClass<out Any>>> = mutableMapOf()
    _typeConvertersMap.put(WorkoutDao::class, WorkoutDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ExerciseDao::class, ExerciseDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(WorkoutExerciseDao::class,
        WorkoutExerciseDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SetDao::class, SetDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(UserProfileDao::class, UserProfileDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FoodLogDao::class, FoodLogDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun workoutDao(): WorkoutDao = _workoutDao.value

  public override fun exerciseDao(): ExerciseDao = _exerciseDao.value

  public override fun workoutExerciseDao(): WorkoutExerciseDao = _workoutExerciseDao.value

  public override fun setDao(): SetDao = _setDao.value

  public override fun userProfileDao(): UserProfileDao = _userProfileDao.value

  public override fun foodLogDao(): FoodLogDao = _foodLogDao.value
}
