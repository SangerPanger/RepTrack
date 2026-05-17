package com.example.fitnessapp.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.fitnessapp.`data`.local.entity.ExerciseEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ExerciseDao_Impl(
  __db: RoomDatabase,
) : ExerciseDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfExerciseEntity: EntityInsertAdapter<ExerciseEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfExerciseEntity = object : EntityInsertAdapter<ExerciseEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `exercises` (`id`,`name`,`muscleGroup`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ExerciseEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        val _tmpMuscleGroup: String? = entity.muscleGroup
        if (_tmpMuscleGroup == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpMuscleGroup)
        }
      }
    }
  }

  public override suspend fun insertExercise(exercise: ExerciseEntity): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfExerciseEntity.insertAndReturnId(_connection, exercise)
    _result
  }

  public override fun getAllExercises(): Flow<List<ExerciseEntity>> {
    val _sql: String = "SELECT * FROM exercises ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("exercises")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfMuscleGroup: Int = getColumnIndexOrThrow(_stmt, "muscleGroup")
        val _result: MutableList<ExerciseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExerciseEntity
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
          _item = ExerciseEntity(_tmpId,_tmpName,_tmpMuscleGroup)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getExerciseById(id: Long): ExerciseEntity? {
    val _sql: String = "SELECT * FROM exercises WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfMuscleGroup: Int = getColumnIndexOrThrow(_stmt, "muscleGroup")
        val _result: ExerciseEntity?
        if (_stmt.step()) {
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
          _result = ExerciseEntity(_tmpId,_tmpName,_tmpMuscleGroup)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getExerciseByNameIgnoreCase(name: String): ExerciseEntity? {
    val _sql: String = "SELECT * FROM exercises WHERE LOWER(name) = LOWER(?) LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, name)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfMuscleGroup: Int = getColumnIndexOrThrow(_stmt, "muscleGroup")
        val _result: ExerciseEntity?
        if (_stmt.step()) {
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
          _result = ExerciseEntity(_tmpId,_tmpName,_tmpMuscleGroup)
        } else {
          _result = null
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
