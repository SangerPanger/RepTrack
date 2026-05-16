package com.example.fitnessapp.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.fitnessapp.`data`.local.entity.SetEntity
import com.example.fitnessapp.`data`.local.entity.SetWithDate
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
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SetDao_Impl(
  __db: RoomDatabase,
) : SetDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSetEntity: EntityInsertAdapter<SetEntity>

  private val __deleteAdapterOfSetEntity: EntityDeleteOrUpdateAdapter<SetEntity>

  private val __updateAdapterOfSetEntity: EntityDeleteOrUpdateAdapter<SetEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSetEntity = object : EntityInsertAdapter<SetEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `sets` (`id`,`workoutExerciseId`,`setNumber`,`reps`,`weight`,`rpe`,`isDrop`,`completedAt`,`completed`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SetEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.workoutExerciseId)
        statement.bindLong(3, entity.setNumber.toLong())
        statement.bindLong(4, entity.reps.toLong())
        statement.bindDouble(5, entity.weight)
        val _tmpRpe: Int? = entity.rpe
        if (_tmpRpe == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpRpe.toLong())
        }
        val _tmp: Int = if (entity.isDrop) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpCompletedAt)
        }
        val _tmp_1: Int = if (entity.completed) 1 else 0
        statement.bindLong(9, _tmp_1.toLong())
      }
    }
    this.__deleteAdapterOfSetEntity = object : EntityDeleteOrUpdateAdapter<SetEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `sets` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SetEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfSetEntity = object : EntityDeleteOrUpdateAdapter<SetEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `sets` SET `id` = ?,`workoutExerciseId` = ?,`setNumber` = ?,`reps` = ?,`weight` = ?,`rpe` = ?,`isDrop` = ?,`completedAt` = ?,`completed` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SetEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.workoutExerciseId)
        statement.bindLong(3, entity.setNumber.toLong())
        statement.bindLong(4, entity.reps.toLong())
        statement.bindDouble(5, entity.weight)
        val _tmpRpe: Int? = entity.rpe
        if (_tmpRpe == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpRpe.toLong())
        }
        val _tmp: Int = if (entity.isDrop) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpCompletedAt)
        }
        val _tmp_1: Int = if (entity.completed) 1 else 0
        statement.bindLong(9, _tmp_1.toLong())
        statement.bindLong(10, entity.id)
      }
    }
  }

  public override suspend fun insertSet(`set`: SetEntity): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfSetEntity.insertAndReturnId(_connection, set)
    _result
  }

  public override suspend fun deleteSet(`set`: SetEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __deleteAdapterOfSetEntity.handle(_connection, set)
  }

  public override suspend fun updateSet(`set`: SetEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfSetEntity.handle(_connection, set)
  }

  public override fun getSetsForWorkoutExercise(workoutExerciseId: Long): Flow<List<SetEntity>> {
    val _sql: String = "SELECT * FROM sets WHERE workoutExerciseId = ? ORDER BY setNumber"
    return createFlow(__db, false, arrayOf("sets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, workoutExerciseId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfWorkoutExerciseId: Int = getColumnIndexOrThrow(_stmt, "workoutExerciseId")
        val _cursorIndexOfSetNumber: Int = getColumnIndexOrThrow(_stmt, "setNumber")
        val _cursorIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _cursorIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _cursorIndexOfRpe: Int = getColumnIndexOrThrow(_stmt, "rpe")
        val _cursorIndexOfIsDrop: Int = getColumnIndexOrThrow(_stmt, "isDrop")
        val _cursorIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _cursorIndexOfCompleted: Int = getColumnIndexOrThrow(_stmt, "completed")
        val _result: MutableList<SetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SetEntity
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
          _item =
              SetEntity(_tmpId,_tmpWorkoutExerciseId,_tmpSetNumber,_tmpReps,_tmpWeight,_tmpRpe,_tmpIsDrop,_tmpCompletedAt,_tmpCompleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllSetsForExerciseWithDate(exerciseId: Long): Flow<List<SetWithDate>> {
    val _sql: String = """
        |
        |        SELECT s.*, w.startedAt, we.startingWeight
        |        FROM sets s 
        |        JOIN workout_exercises we ON s.workoutExerciseId = we.id 
        |        JOIN workouts w ON we.workoutId = w.id
        |        WHERE we.exerciseId = ? 
        |        ORDER BY w.startedAt ASC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("sets", "workout_exercises", "workouts")) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, exerciseId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfWorkoutExerciseId: Int = getColumnIndexOrThrow(_stmt, "workoutExerciseId")
        val _cursorIndexOfSetNumber: Int = getColumnIndexOrThrow(_stmt, "setNumber")
        val _cursorIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _cursorIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _cursorIndexOfRpe: Int = getColumnIndexOrThrow(_stmt, "rpe")
        val _cursorIndexOfIsDrop: Int = getColumnIndexOrThrow(_stmt, "isDrop")
        val _cursorIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _cursorIndexOfCompleted: Int = getColumnIndexOrThrow(_stmt, "completed")
        val _cursorIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _cursorIndexOfStartingWeight: Int = getColumnIndexOrThrow(_stmt, "startingWeight")
        val _result: MutableList<SetWithDate> = mutableListOf()
        while (_stmt.step()) {
          val _item: SetWithDate
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_cursorIndexOfStartedAt)
          val _tmpStartingWeight: Double
          _tmpStartingWeight = _stmt.getDouble(_cursorIndexOfStartingWeight)
          val _tmpSetEntity: SetEntity
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
          _tmpSetEntity =
              SetEntity(_tmpId,_tmpWorkoutExerciseId,_tmpSetNumber,_tmpReps,_tmpWeight,_tmpRpe,_tmpIsDrop,_tmpCompletedAt,_tmpCompleted)
          _item = SetWithDate(_tmpSetEntity,_tmpStartedAt,_tmpStartingWeight)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllSetsForExercise(exerciseId: Long): Flow<List<SetEntity>> {
    val _sql: String =
        "SELECT * FROM sets s JOIN workout_exercises we ON s.workoutExerciseId = we.id WHERE we.exerciseId = ? ORDER BY s.id DESC"
    return createFlow(__db, false, arrayOf("sets", "workout_exercises")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, exerciseId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfWorkoutExerciseId: Int = getColumnIndexOrThrow(_stmt, "workoutExerciseId")
        val _cursorIndexOfSetNumber: Int = getColumnIndexOrThrow(_stmt, "setNumber")
        val _cursorIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _cursorIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _cursorIndexOfRpe: Int = getColumnIndexOrThrow(_stmt, "rpe")
        val _cursorIndexOfIsDrop: Int = getColumnIndexOrThrow(_stmt, "isDrop")
        val _cursorIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _cursorIndexOfCompleted: Int = getColumnIndexOrThrow(_stmt, "completed")
        val _result: MutableList<SetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SetEntity
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
          _item =
              SetEntity(_tmpId,_tmpWorkoutExerciseId,_tmpSetNumber,_tmpReps,_tmpWeight,_tmpRpe,_tmpIsDrop,_tmpCompletedAt,_tmpCompleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSetsForWorkout(workoutId: Long): List<SetEntity> {
    val _sql: String =
        "SELECT s.* FROM sets s JOIN workout_exercises we ON s.workoutExerciseId = we.id WHERE we.workoutId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, workoutId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfWorkoutExerciseId: Int = getColumnIndexOrThrow(_stmt, "workoutExerciseId")
        val _cursorIndexOfSetNumber: Int = getColumnIndexOrThrow(_stmt, "setNumber")
        val _cursorIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _cursorIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _cursorIndexOfRpe: Int = getColumnIndexOrThrow(_stmt, "rpe")
        val _cursorIndexOfIsDrop: Int = getColumnIndexOrThrow(_stmt, "isDrop")
        val _cursorIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _cursorIndexOfCompleted: Int = getColumnIndexOrThrow(_stmt, "completed")
        val _result: MutableList<SetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SetEntity
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
          _item =
              SetEntity(_tmpId,_tmpWorkoutExerciseId,_tmpSetNumber,_tmpReps,_tmpWeight,_tmpRpe,_tmpIsDrop,_tmpCompletedAt,_tmpCompleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
