package com.example.fitnessapp.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.fitnessapp.`data`.local.entity.UserProfileEntity
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class UserProfileDao_Impl(
  __db: RoomDatabase,
) : UserProfileDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUserProfileEntity: EntityInsertAdapter<UserProfileEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfUserProfileEntity = object : EntityInsertAdapter<UserProfileEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `user_profile` (`id`,`age`,`gender`,`height`,`currentWeight`,`targetWeight`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UserProfileEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.age.toLong())
        statement.bindText(3, entity.gender)
        statement.bindDouble(4, entity.height)
        statement.bindDouble(5, entity.currentWeight)
        statement.bindDouble(6, entity.targetWeight)
      }
    }
  }

  public override suspend fun insertOrUpdate(profile: UserProfileEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfUserProfileEntity.insert(_connection, profile)
  }

  public override fun getUserProfile(): Flow<UserProfileEntity?> {
    val _sql: String = "SELECT * FROM user_profile WHERE id = 1"
    return createFlow(__db, false, arrayOf("user_profile")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfAge: Int = getColumnIndexOrThrow(_stmt, "age")
        val _cursorIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _cursorIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _cursorIndexOfCurrentWeight: Int = getColumnIndexOrThrow(_stmt, "currentWeight")
        val _cursorIndexOfTargetWeight: Int = getColumnIndexOrThrow(_stmt, "targetWeight")
        val _result: UserProfileEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpAge: Int
          _tmpAge = _stmt.getLong(_cursorIndexOfAge).toInt()
          val _tmpGender: String
          _tmpGender = _stmt.getText(_cursorIndexOfGender)
          val _tmpHeight: Double
          _tmpHeight = _stmt.getDouble(_cursorIndexOfHeight)
          val _tmpCurrentWeight: Double
          _tmpCurrentWeight = _stmt.getDouble(_cursorIndexOfCurrentWeight)
          val _tmpTargetWeight: Double
          _tmpTargetWeight = _stmt.getDouble(_cursorIndexOfTargetWeight)
          _result =
              UserProfileEntity(_tmpId,_tmpAge,_tmpGender,_tmpHeight,_tmpCurrentWeight,_tmpTargetWeight)
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
