package com.example.fitnessapp.`data`.local.dao

import androidx.collection.LongSparseArray
import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndex
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.room.util.recursiveFetchLongSparseArray
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import com.example.fitnessapp.`data`.local.entity.ExerciseEntity
import com.example.fitnessapp.`data`.local.entity.SetEntity
import com.example.fitnessapp.`data`.local.entity.WorkoutEntity
import com.example.fitnessapp.`data`.local.entity.WorkoutExerciseEntity
import com.example.fitnessapp.`data`.local.entity.WorkoutExerciseWithSets
import com.example.fitnessapp.`data`.local.entity.WorkoutWithExercises
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class WorkoutDao_Impl(
  __db: RoomDatabase,
) : WorkoutDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWorkoutEntity: EntityInsertAdapter<WorkoutEntity>

  private val __deleteAdapterOfWorkoutEntity: EntityDeleteOrUpdateAdapter<WorkoutEntity>

  private val __updateAdapterOfWorkoutEntity: EntityDeleteOrUpdateAdapter<WorkoutEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWorkoutEntity = object : EntityInsertAdapter<WorkoutEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `workouts` (`id`,`title`,`startedAt`,`finishedAt`,`notes`,`manualDurationMinutes`,`durationOffsetMs`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WorkoutEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindLong(3, entity.startedAt)
        val _tmpFinishedAt: Long? = entity.finishedAt
        if (_tmpFinishedAt == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpFinishedAt)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpNotes)
        }
        val _tmpManualDurationMinutes: Long? = entity.manualDurationMinutes
        if (_tmpManualDurationMinutes == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpManualDurationMinutes)
        }
        statement.bindLong(7, entity.durationOffsetMs)
      }
    }
    this.__deleteAdapterOfWorkoutEntity = object : EntityDeleteOrUpdateAdapter<WorkoutEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `workouts` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: WorkoutEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfWorkoutEntity = object : EntityDeleteOrUpdateAdapter<WorkoutEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `workouts` SET `id` = ?,`title` = ?,`startedAt` = ?,`finishedAt` = ?,`notes` = ?,`manualDurationMinutes` = ?,`durationOffsetMs` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: WorkoutEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindLong(3, entity.startedAt)
        val _tmpFinishedAt: Long? = entity.finishedAt
        if (_tmpFinishedAt == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpFinishedAt)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpNotes)
        }
        val _tmpManualDurationMinutes: Long? = entity.manualDurationMinutes
        if (_tmpManualDurationMinutes == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpManualDurationMinutes)
        }
        statement.bindLong(7, entity.durationOffsetMs)
        statement.bindLong(8, entity.id)
      }
    }
  }

  public override suspend fun insertWorkout(workout: WorkoutEntity): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfWorkoutEntity.insertAndReturnId(_connection, workout)
    _result
  }

  public override suspend fun deleteWorkout(workout: WorkoutEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfWorkoutEntity.handle(_connection, workout)
  }

  public override suspend fun updateWorkout(workout: WorkoutEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfWorkoutEntity.handle(_connection, workout)
  }

  public override fun getAllWorkoutsWithExercises(): Flow<List<WorkoutWithExercises>> {
    val _sql: String = "SELECT * FROM workouts ORDER BY startedAt DESC"
    return createFlow(__db, true, arrayOf("exercises", "sets", "workout_exercises", "workouts")) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _cursorIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _cursorIndexOfFinishedAt: Int = getColumnIndexOrThrow(_stmt, "finishedAt")
        val _cursorIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _cursorIndexOfManualDurationMinutes: Int = getColumnIndexOrThrow(_stmt,
            "manualDurationMinutes")
        val _cursorIndexOfDurationOffsetMs: Int = getColumnIndexOrThrow(_stmt, "durationOffsetMs")
        val _collectionWorkoutExercises: LongSparseArray<MutableList<WorkoutExerciseWithSets>> =
            LongSparseArray<MutableList<WorkoutExerciseWithSets>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_cursorIndexOfId)
          if (!_collectionWorkoutExercises.containsKey(_tmpKey)) {
            _collectionWorkoutExercises.put(_tmpKey, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshipworkoutExercisesAscomExampleFitnessappDataLocalEntityWorkoutExerciseWithSets(_connection,
            _collectionWorkoutExercises)
        val _result: MutableList<WorkoutWithExercises> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutWithExercises
          val _tmpWorkout: WorkoutEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_cursorIndexOfTitle)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_cursorIndexOfStartedAt)
          val _tmpFinishedAt: Long?
          if (_stmt.isNull(_cursorIndexOfFinishedAt)) {
            _tmpFinishedAt = null
          } else {
            _tmpFinishedAt = _stmt.getLong(_cursorIndexOfFinishedAt)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_cursorIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_cursorIndexOfNotes)
          }
          val _tmpManualDurationMinutes: Long?
          if (_stmt.isNull(_cursorIndexOfManualDurationMinutes)) {
            _tmpManualDurationMinutes = null
          } else {
            _tmpManualDurationMinutes = _stmt.getLong(_cursorIndexOfManualDurationMinutes)
          }
          val _tmpDurationOffsetMs: Long
          _tmpDurationOffsetMs = _stmt.getLong(_cursorIndexOfDurationOffsetMs)
          _tmpWorkout =
              WorkoutEntity(_tmpId,_tmpTitle,_tmpStartedAt,_tmpFinishedAt,_tmpNotes,_tmpManualDurationMinutes,_tmpDurationOffsetMs)
          val _tmpWorkoutExercisesCollection: MutableList<WorkoutExerciseWithSets>
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_cursorIndexOfId)
          _tmpWorkoutExercisesCollection = checkNotNull(_collectionWorkoutExercises.get(_tmpKey_1))
          _item = WorkoutWithExercises(_tmpWorkout,_tmpWorkoutExercisesCollection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllWorkouts(): Flow<List<WorkoutEntity>> {
    val _sql: String = "SELECT * FROM workouts ORDER BY startedAt DESC"
    return createFlow(__db, false, arrayOf("workouts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _cursorIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _cursorIndexOfFinishedAt: Int = getColumnIndexOrThrow(_stmt, "finishedAt")
        val _cursorIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _cursorIndexOfManualDurationMinutes: Int = getColumnIndexOrThrow(_stmt,
            "manualDurationMinutes")
        val _cursorIndexOfDurationOffsetMs: Int = getColumnIndexOrThrow(_stmt, "durationOffsetMs")
        val _result: MutableList<WorkoutEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_cursorIndexOfTitle)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_cursorIndexOfStartedAt)
          val _tmpFinishedAt: Long?
          if (_stmt.isNull(_cursorIndexOfFinishedAt)) {
            _tmpFinishedAt = null
          } else {
            _tmpFinishedAt = _stmt.getLong(_cursorIndexOfFinishedAt)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_cursorIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_cursorIndexOfNotes)
          }
          val _tmpManualDurationMinutes: Long?
          if (_stmt.isNull(_cursorIndexOfManualDurationMinutes)) {
            _tmpManualDurationMinutes = null
          } else {
            _tmpManualDurationMinutes = _stmt.getLong(_cursorIndexOfManualDurationMinutes)
          }
          val _tmpDurationOffsetMs: Long
          _tmpDurationOffsetMs = _stmt.getLong(_cursorIndexOfDurationOffsetMs)
          _item =
              WorkoutEntity(_tmpId,_tmpTitle,_tmpStartedAt,_tmpFinishedAt,_tmpNotes,_tmpManualDurationMinutes,_tmpDurationOffsetMs)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getWorkoutById(id: Long): WorkoutEntity? {
    val _sql: String = "SELECT * FROM workouts WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _cursorIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _cursorIndexOfFinishedAt: Int = getColumnIndexOrThrow(_stmt, "finishedAt")
        val _cursorIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _cursorIndexOfManualDurationMinutes: Int = getColumnIndexOrThrow(_stmt,
            "manualDurationMinutes")
        val _cursorIndexOfDurationOffsetMs: Int = getColumnIndexOrThrow(_stmt, "durationOffsetMs")
        val _result: WorkoutEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_cursorIndexOfTitle)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_cursorIndexOfStartedAt)
          val _tmpFinishedAt: Long?
          if (_stmt.isNull(_cursorIndexOfFinishedAt)) {
            _tmpFinishedAt = null
          } else {
            _tmpFinishedAt = _stmt.getLong(_cursorIndexOfFinishedAt)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_cursorIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_cursorIndexOfNotes)
          }
          val _tmpManualDurationMinutes: Long?
          if (_stmt.isNull(_cursorIndexOfManualDurationMinutes)) {
            _tmpManualDurationMinutes = null
          } else {
            _tmpManualDurationMinutes = _stmt.getLong(_cursorIndexOfManualDurationMinutes)
          }
          val _tmpDurationOffsetMs: Long
          _tmpDurationOffsetMs = _stmt.getLong(_cursorIndexOfDurationOffsetMs)
          _result =
              WorkoutEntity(_tmpId,_tmpTitle,_tmpStartedAt,_tmpFinishedAt,_tmpNotes,_tmpManualDurationMinutes,_tmpDurationOffsetMs)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getWorkoutFlow(id: Long): Flow<WorkoutEntity?> {
    val _sql: String = "SELECT * FROM workouts WHERE id = ?"
    return createFlow(__db, false, arrayOf("workouts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _cursorIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _cursorIndexOfFinishedAt: Int = getColumnIndexOrThrow(_stmt, "finishedAt")
        val _cursorIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _cursorIndexOfManualDurationMinutes: Int = getColumnIndexOrThrow(_stmt,
            "manualDurationMinutes")
        val _cursorIndexOfDurationOffsetMs: Int = getColumnIndexOrThrow(_stmt, "durationOffsetMs")
        val _result: WorkoutEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_cursorIndexOfTitle)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_cursorIndexOfStartedAt)
          val _tmpFinishedAt: Long?
          if (_stmt.isNull(_cursorIndexOfFinishedAt)) {
            _tmpFinishedAt = null
          } else {
            _tmpFinishedAt = _stmt.getLong(_cursorIndexOfFinishedAt)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_cursorIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_cursorIndexOfNotes)
          }
          val _tmpManualDurationMinutes: Long?
          if (_stmt.isNull(_cursorIndexOfManualDurationMinutes)) {
            _tmpManualDurationMinutes = null
          } else {
            _tmpManualDurationMinutes = _stmt.getLong(_cursorIndexOfManualDurationMinutes)
          }
          val _tmpDurationOffsetMs: Long
          _tmpDurationOffsetMs = _stmt.getLong(_cursorIndexOfDurationOffsetMs)
          _result =
              WorkoutEntity(_tmpId,_tmpTitle,_tmpStartedAt,_tmpFinishedAt,_tmpNotes,_tmpManualDurationMinutes,_tmpDurationOffsetMs)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getLatestWorkout(): Flow<WorkoutEntity?> {
    val _sql: String = "SELECT * FROM workouts ORDER BY startedAt DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("workouts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _cursorIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _cursorIndexOfFinishedAt: Int = getColumnIndexOrThrow(_stmt, "finishedAt")
        val _cursorIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _cursorIndexOfManualDurationMinutes: Int = getColumnIndexOrThrow(_stmt,
            "manualDurationMinutes")
        val _cursorIndexOfDurationOffsetMs: Int = getColumnIndexOrThrow(_stmt, "durationOffsetMs")
        val _result: WorkoutEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_cursorIndexOfTitle)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_cursorIndexOfStartedAt)
          val _tmpFinishedAt: Long?
          if (_stmt.isNull(_cursorIndexOfFinishedAt)) {
            _tmpFinishedAt = null
          } else {
            _tmpFinishedAt = _stmt.getLong(_cursorIndexOfFinishedAt)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_cursorIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_cursorIndexOfNotes)
          }
          val _tmpManualDurationMinutes: Long?
          if (_stmt.isNull(_cursorIndexOfManualDurationMinutes)) {
            _tmpManualDurationMinutes = null
          } else {
            _tmpManualDurationMinutes = _stmt.getLong(_cursorIndexOfManualDurationMinutes)
          }
          val _tmpDurationOffsetMs: Long
          _tmpDurationOffsetMs = _stmt.getLong(_cursorIndexOfDurationOffsetMs)
          _result =
              WorkoutEntity(_tmpId,_tmpTitle,_tmpStartedAt,_tmpFinishedAt,_tmpNotes,_tmpManualDurationMinutes,_tmpDurationOffsetMs)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getWorkoutCount(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM workouts"
    return createFlow(__db, false, arrayOf("workouts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUniqueWorkoutTitles(): Flow<List<String>> {
    val _sql: String =
        "SELECT DISTINCT title FROM workouts WHERE title IS NOT NULL AND title != '' ORDER BY title ASC"
    return createFlow(__db, false, arrayOf("workouts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLastWorkoutByTitle(title: String): WorkoutEntity? {
    val _sql: String = "SELECT * FROM workouts WHERE title = ? ORDER BY startedAt DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, title)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _cursorIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _cursorIndexOfFinishedAt: Int = getColumnIndexOrThrow(_stmt, "finishedAt")
        val _cursorIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _cursorIndexOfManualDurationMinutes: Int = getColumnIndexOrThrow(_stmt,
            "manualDurationMinutes")
        val _cursorIndexOfDurationOffsetMs: Int = getColumnIndexOrThrow(_stmt, "durationOffsetMs")
        val _result: WorkoutEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_cursorIndexOfTitle)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_cursorIndexOfStartedAt)
          val _tmpFinishedAt: Long?
          if (_stmt.isNull(_cursorIndexOfFinishedAt)) {
            _tmpFinishedAt = null
          } else {
            _tmpFinishedAt = _stmt.getLong(_cursorIndexOfFinishedAt)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_cursorIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_cursorIndexOfNotes)
          }
          val _tmpManualDurationMinutes: Long?
          if (_stmt.isNull(_cursorIndexOfManualDurationMinutes)) {
            _tmpManualDurationMinutes = null
          } else {
            _tmpManualDurationMinutes = _stmt.getLong(_cursorIndexOfManualDurationMinutes)
          }
          val _tmpDurationOffsetMs: Long
          _tmpDurationOffsetMs = _stmt.getLong(_cursorIndexOfDurationOffsetMs)
          _result =
              WorkoutEntity(_tmpId,_tmpTitle,_tmpStartedAt,_tmpFinishedAt,_tmpNotes,_tmpManualDurationMinutes,_tmpDurationOffsetMs)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  private
      fun __fetchRelationshipexercisesAscomExampleFitnessappDataLocalEntityExerciseEntity(_connection: SQLiteConnection,
      _map: LongSparseArray<ExerciseEntity?>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, false) { _tmpMap ->
        __fetchRelationshipexercisesAscomExampleFitnessappDataLocalEntityExerciseEntity(_connection,
            _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `id`,`name`,`muscleGroup` FROM `exercises` WHERE `id` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    try {
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "id")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfName: Int = 1
      val _cursorIndexOfMuscleGroup: Int = 2
      while (_stmt.step()) {
        val _tmpKey: Long
        _tmpKey = _stmt.getLong(_itemKeyIndex)
        if (_map.containsKey(_tmpKey)) {
          val _item_1: ExerciseEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpMuscleGroup: String?
          if (_stmt.isNull(_cursorIndexOfMuscleGroup)) {
            _tmpMuscleGroup = null
          } else {
            _tmpMuscleGroup = _stmt.getText(_cursorIndexOfMuscleGroup)
          }
          _item_1 = ExerciseEntity(_tmpId,_tmpName,_tmpMuscleGroup)
          _map.put(_tmpKey, _item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  private
      fun __fetchRelationshipsetsAscomExampleFitnessappDataLocalEntitySetEntity(_connection: SQLiteConnection,
      _map: LongSparseArray<MutableList<SetEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, true) { _tmpMap ->
        __fetchRelationshipsetsAscomExampleFitnessappDataLocalEntitySetEntity(_connection, _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `id`,`workoutExerciseId`,`setNumber`,`reps`,`weight`,`rpe`,`isDrop`,`completedAt`,`completed` FROM `sets` WHERE `workoutExerciseId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    try {
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "workoutExerciseId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfWorkoutExerciseId: Int = 1
      val _cursorIndexOfSetNumber: Int = 2
      val _cursorIndexOfReps: Int = 3
      val _cursorIndexOfWeight: Int = 4
      val _cursorIndexOfRpe: Int = 5
      val _cursorIndexOfIsDrop: Int = 6
      val _cursorIndexOfCompletedAt: Int = 7
      val _cursorIndexOfCompleted: Int = 8
      while (_stmt.step()) {
        val _tmpKey: Long
        _tmpKey = _stmt.getLong(_itemKeyIndex)
        val _tmpRelation: MutableList<SetEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: SetEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpWorkoutExerciseId: Long
          _tmpWorkoutExerciseId = _stmt.getLong(_cursorIndexOfWorkoutExerciseId)
          val _tmpSetNumber: Int
          _tmpSetNumber = _stmt.getLong(_cursorIndexOfSetNumber).toInt()
          val _tmpReps: Int
          _tmpReps = _stmt.getLong(_cursorIndexOfReps).toInt()
          val _tmpWeight: Double
          _tmpWeight = _stmt.getDouble(_cursorIndexOfWeight)
          val _tmpRpe: Int?
          if (_stmt.isNull(_cursorIndexOfRpe)) {
            _tmpRpe = null
          } else {
            _tmpRpe = _stmt.getLong(_cursorIndexOfRpe).toInt()
          }
          val _tmpIsDrop: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_cursorIndexOfIsDrop).toInt()
          _tmpIsDrop = _tmp != 0
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_cursorIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_cursorIndexOfCompletedAt)
          }
          val _tmpCompleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_cursorIndexOfCompleted).toInt()
          _tmpCompleted = _tmp_1 != 0
          _item_1 =
              SetEntity(_tmpId,_tmpWorkoutExerciseId,_tmpSetNumber,_tmpReps,_tmpWeight,_tmpRpe,_tmpIsDrop,_tmpCompletedAt,_tmpCompleted)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  private
      fun __fetchRelationshipworkoutExercisesAscomExampleFitnessappDataLocalEntityWorkoutExerciseWithSets(_connection: SQLiteConnection,
      _map: LongSparseArray<MutableList<WorkoutExerciseWithSets>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, true) { _tmpMap ->
        __fetchRelationshipworkoutExercisesAscomExampleFitnessappDataLocalEntityWorkoutExerciseWithSets(_connection,
            _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `id`,`workoutId`,`exerciseId`,`orderIndex`,`isDropset`,`startingWeight`,`dropWeightDecrease` FROM `workout_exercises` WHERE `workoutId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    try {
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "workoutId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfWorkoutId: Int = 1
      val _cursorIndexOfExerciseId: Int = 2
      val _cursorIndexOfOrderIndex: Int = 3
      val _cursorIndexOfIsDropset: Int = 4
      val _cursorIndexOfStartingWeight: Int = 5
      val _cursorIndexOfDropWeightDecrease: Int = 6
      val _collectionExercise: LongSparseArray<ExerciseEntity?> = LongSparseArray<ExerciseEntity?>()
      val _collectionSets: LongSparseArray<MutableList<SetEntity>> =
          LongSparseArray<MutableList<SetEntity>>()
      while (_stmt.step()) {
        val _tmpKey: Long
        _tmpKey = _stmt.getLong(_cursorIndexOfExerciseId)
        _collectionExercise.put(_tmpKey, null)
        val _tmpKey_1: Long
        _tmpKey_1 = _stmt.getLong(_cursorIndexOfId)
        if (!_collectionSets.containsKey(_tmpKey_1)) {
          _collectionSets.put(_tmpKey_1, mutableListOf())
        }
      }
      _stmt.reset()
      __fetchRelationshipexercisesAscomExampleFitnessappDataLocalEntityExerciseEntity(_connection,
          _collectionExercise)
      __fetchRelationshipsetsAscomExampleFitnessappDataLocalEntitySetEntity(_connection,
          _collectionSets)
      while (_stmt.step()) {
        val _tmpKey_2: Long
        _tmpKey_2 = _stmt.getLong(_itemKeyIndex)
        val _tmpRelation: MutableList<WorkoutExerciseWithSets>? = _map.get(_tmpKey_2)
        if (_tmpRelation != null) {
          val _item_1: WorkoutExerciseWithSets
          val _tmpWorkoutExercise: WorkoutExerciseEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpWorkoutId: Long
          _tmpWorkoutId = _stmt.getLong(_cursorIndexOfWorkoutId)
          val _tmpExerciseId: Long
          _tmpExerciseId = _stmt.getLong(_cursorIndexOfExerciseId)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_cursorIndexOfOrderIndex).toInt()
          val _tmpIsDropset: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_cursorIndexOfIsDropset).toInt()
          _tmpIsDropset = _tmp != 0
          val _tmpStartingWeight: Double
          _tmpStartingWeight = _stmt.getDouble(_cursorIndexOfStartingWeight)
          val _tmpDropWeightDecrease: Double
          _tmpDropWeightDecrease = _stmt.getDouble(_cursorIndexOfDropWeightDecrease)
          _tmpWorkoutExercise =
              WorkoutExerciseEntity(_tmpId,_tmpWorkoutId,_tmpExerciseId,_tmpOrderIndex,_tmpIsDropset,_tmpStartingWeight,_tmpDropWeightDecrease)
          val _tmpExercise: ExerciseEntity?
          val _tmpKey_3: Long
          _tmpKey_3 = _stmt.getLong(_cursorIndexOfExerciseId)
          _tmpExercise = _collectionExercise.get(_tmpKey_3)
          if (_tmpExercise == null) {
            error("Relationship item 'exercise' was expected to be NON-NULL but is NULL in @Relation involving a parent column named 'exerciseId' and entityColumn named 'id'.")
          }
          val _tmpSetsCollection: MutableList<SetEntity>
          val _tmpKey_4: Long
          _tmpKey_4 = _stmt.getLong(_cursorIndexOfId)
          _tmpSetsCollection = checkNotNull(_collectionSets.get(_tmpKey_4))
          _item_1 = WorkoutExerciseWithSets(_tmpWorkoutExercise,_tmpExercise,_tmpSetsCollection)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
