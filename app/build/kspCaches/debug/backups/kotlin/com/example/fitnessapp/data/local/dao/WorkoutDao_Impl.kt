package com.example.fitnessapp.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.fitnessapp.`data`.local.entity.WorkoutEntity
import javax.`annotation`.processing.Generated
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
          "INSERT OR REPLACE INTO `workouts` (`id`,`title`,`startedAt`,`finishedAt`,`notes`) VALUES (nullif(?, 0),?,?,?,?)"

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
          "UPDATE OR ABORT `workouts` SET `id` = ?,`title` = ?,`startedAt` = ?,`finishedAt` = ?,`notes` = ? WHERE `id` = ?"

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
        statement.bindLong(6, entity.id)
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
          _item = WorkoutEntity(_tmpId,_tmpTitle,_tmpStartedAt,_tmpFinishedAt,_tmpNotes)
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
          _result = WorkoutEntity(_tmpId,_tmpTitle,_tmpStartedAt,_tmpFinishedAt,_tmpNotes)
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
          _result = WorkoutEntity(_tmpId,_tmpTitle,_tmpStartedAt,_tmpFinishedAt,_tmpNotes)
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
