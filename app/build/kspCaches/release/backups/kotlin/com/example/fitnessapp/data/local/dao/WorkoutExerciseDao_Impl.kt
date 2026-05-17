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
import com.example.fitnessapp.`data`.local.entity.WorkoutExerciseEntity
import com.example.fitnessapp.`data`.local.entity.WorkoutExerciseWithSets
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
public class WorkoutExerciseDao_Impl(
  __db: RoomDatabase,
) : WorkoutExerciseDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWorkoutExerciseEntity: EntityInsertAdapter<WorkoutExerciseEntity>

  private val __deleteAdapterOfWorkoutExerciseEntity:
      EntityDeleteOrUpdateAdapter<WorkoutExerciseEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWorkoutExerciseEntity = object :
        EntityInsertAdapter<WorkoutExerciseEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `workout_exercises` (`id`,`workoutId`,`exerciseId`,`orderIndex`,`isDropset`,`startingWeight`,`dropWeightDecrease`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WorkoutExerciseEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.workoutId)
        statement.bindLong(3, entity.exerciseId)
        statement.bindLong(4, entity.orderIndex.toLong())
        val _tmp: Int = if (entity.isDropset) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        statement.bindDouble(6, entity.startingWeight)
        statement.bindDouble(7, entity.dropWeightDecrease)
      }
    }
    this.__deleteAdapterOfWorkoutExerciseEntity = object :
        EntityDeleteOrUpdateAdapter<WorkoutExerciseEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `workout_exercises` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: WorkoutExerciseEntity) {
        statement.bindLong(1, entity.id)
      }
    }
  }

  public override suspend fun insertWorkoutExercise(workoutExercise: WorkoutExerciseEntity): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfWorkoutExerciseEntity.insertAndReturnId(_connection,
        workoutExercise)
    _result
  }

  public override suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExerciseEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfWorkoutExerciseEntity.handle(_connection, workoutExercise)
  }

  public override fun getWorkoutExercisesWithSets(workoutId: Long):
      Flow<List<WorkoutExerciseWithSets>> {
    val _sql: String = "SELECT * FROM workout_exercises WHERE workoutId = ? ORDER BY orderIndex"
    return createFlow(__db, true, arrayOf("exercises", "sets", "workout_exercises")) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, workoutId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfWorkoutId: Int = getColumnIndexOrThrow(_stmt, "workoutId")
        val _cursorIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exerciseId")
        val _cursorIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "orderIndex")
        val _cursorIndexOfIsDropset: Int = getColumnIndexOrThrow(_stmt, "isDropset")
        val _cursorIndexOfStartingWeight: Int = getColumnIndexOrThrow(_stmt, "startingWeight")
        val _cursorIndexOfDropWeightDecrease: Int = getColumnIndexOrThrow(_stmt,
            "dropWeightDecrease")
        val _collectionExercise: LongSparseArray<ExerciseEntity?> =
            LongSparseArray<ExerciseEntity?>()
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
        val _result: MutableList<WorkoutExerciseWithSets> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutExerciseWithSets
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
          val _tmpKey_2: Long
          _tmpKey_2 = _stmt.getLong(_cursorIndexOfExerciseId)
          _tmpExercise = _collectionExercise.get(_tmpKey_2)
          if (_tmpExercise == null) {
            error("Relationship item 'exercise' was expected to be NON-NULL but is NULL in @Relation involving a parent column named 'exerciseId' and entityColumn named 'id'.")
          }
          val _tmpSetsCollection: MutableList<SetEntity>
          val _tmpKey_3: Long
          _tmpKey_3 = _stmt.getLong(_cursorIndexOfId)
          _tmpSetsCollection = checkNotNull(_collectionSets.get(_tmpKey_3))
          _item = WorkoutExerciseWithSets(_tmpWorkoutExercise,_tmpExercise,_tmpSetsCollection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getWorkoutExercisesWithSetsSuspend(workoutId: Long):
      List<WorkoutExerciseWithSets> {
    val _sql: String = "SELECT * FROM workout_exercises WHERE workoutId = ? ORDER BY orderIndex"
    return performSuspending(__db, true, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, workoutId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfWorkoutId: Int = getColumnIndexOrThrow(_stmt, "workoutId")
        val _cursorIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exerciseId")
        val _cursorIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "orderIndex")
        val _cursorIndexOfIsDropset: Int = getColumnIndexOrThrow(_stmt, "isDropset")
        val _cursorIndexOfStartingWeight: Int = getColumnIndexOrThrow(_stmt, "startingWeight")
        val _cursorIndexOfDropWeightDecrease: Int = getColumnIndexOrThrow(_stmt,
            "dropWeightDecrease")
        val _collectionExercise: LongSparseArray<ExerciseEntity?> =
            LongSparseArray<ExerciseEntity?>()
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
        val _result: MutableList<WorkoutExerciseWithSets> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutExerciseWithSets
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
          val _tmpKey_2: Long
          _tmpKey_2 = _stmt.getLong(_cursorIndexOfExerciseId)
          _tmpExercise = _collectionExercise.get(_tmpKey_2)
          if (_tmpExercise == null) {
            error("Relationship item 'exercise' was expected to be NON-NULL but is NULL in @Relation involving a parent column named 'exerciseId' and entityColumn named 'id'.")
          }
          val _tmpSetsCollection: MutableList<SetEntity>
          val _tmpKey_3: Long
          _tmpKey_3 = _stmt.getLong(_cursorIndexOfId)
          _tmpSetsCollection = checkNotNull(_collectionSets.get(_tmpKey_3))
          _item = WorkoutExerciseWithSets(_tmpWorkoutExercise,_tmpExercise,_tmpSetsCollection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getWorkoutExercisesByExercise(exerciseId: Long):
      Flow<List<WorkoutExerciseEntity>> {
    val _sql: String = "SELECT * FROM workout_exercises WHERE exerciseId = ?"
    return createFlow(__db, false, arrayOf("workout_exercises")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, exerciseId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfWorkoutId: Int = getColumnIndexOrThrow(_stmt, "workoutId")
        val _cursorIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exerciseId")
        val _cursorIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "orderIndex")
        val _cursorIndexOfIsDropset: Int = getColumnIndexOrThrow(_stmt, "isDropset")
        val _cursorIndexOfStartingWeight: Int = getColumnIndexOrThrow(_stmt, "startingWeight")
        val _cursorIndexOfDropWeightDecrease: Int = getColumnIndexOrThrow(_stmt,
            "dropWeightDecrease")
        val _result: MutableList<WorkoutExerciseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutExerciseEntity
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
          _item =
              WorkoutExerciseEntity(_tmpId,_tmpWorkoutId,_tmpExerciseId,_tmpOrderIndex,_tmpIsDropset,_tmpStartingWeight,_tmpDropWeightDecrease)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteWorkoutExerciseById(workoutExerciseId: Long) {
    val _sql: String = "DELETE FROM workout_exercises WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, workoutExerciseId)
        _stmt.step()
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
