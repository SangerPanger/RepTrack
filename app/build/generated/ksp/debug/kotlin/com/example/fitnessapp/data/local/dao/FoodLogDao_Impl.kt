package com.example.fitnessapp.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.fitnessapp.`data`.local.entity.FoodLogEntity
import javax.`annotation`.processing.Generated
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
public class FoodLogDao_Impl(
  __db: RoomDatabase,
) : FoodLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFoodLogEntity: EntityInsertAdapter<FoodLogEntity>

  private val __deleteAdapterOfFoodLogEntity: EntityDeleteOrUpdateAdapter<FoodLogEntity>

  private val __updateAdapterOfFoodLogEntity: EntityDeleteOrUpdateAdapter<FoodLogEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFoodLogEntity = object : EntityInsertAdapter<FoodLogEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `food_logs` (`id`,`date`,`carbs`,`fats`,`protein`,`calories`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FoodLogEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.date)
        statement.bindDouble(3, entity.carbs)
        statement.bindDouble(4, entity.fats)
        statement.bindDouble(5, entity.protein)
        statement.bindDouble(6, entity.calories)
      }
    }
    this.__deleteAdapterOfFoodLogEntity = object : EntityDeleteOrUpdateAdapter<FoodLogEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `food_logs` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FoodLogEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfFoodLogEntity = object : EntityDeleteOrUpdateAdapter<FoodLogEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `food_logs` SET `id` = ?,`date` = ?,`carbs` = ?,`fats` = ?,`protein` = ?,`calories` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FoodLogEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.date)
        statement.bindDouble(3, entity.carbs)
        statement.bindDouble(4, entity.fats)
        statement.bindDouble(5, entity.protein)
        statement.bindDouble(6, entity.calories)
        statement.bindLong(7, entity.id)
      }
    }
  }

  public override suspend fun insertFoodLog(foodLog: FoodLogEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfFoodLogEntity.insert(_connection, foodLog)
  }

  public override suspend fun deleteFoodLog(foodLog: FoodLogEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfFoodLogEntity.handle(_connection, foodLog)
  }

  public override suspend fun updateFoodLog(foodLog: FoodLogEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfFoodLogEntity.handle(_connection, foodLog)
  }

  public override fun getAllFoodLogs(): Flow<List<FoodLogEntity>> {
    val _sql: String = "SELECT * FROM food_logs ORDER BY date DESC"
    return createFlow(__db, false, arrayOf("food_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _cursorIndexOfCarbs: Int = getColumnIndexOrThrow(_stmt, "carbs")
        val _cursorIndexOfFats: Int = getColumnIndexOrThrow(_stmt, "fats")
        val _cursorIndexOfProtein: Int = getColumnIndexOrThrow(_stmt, "protein")
        val _cursorIndexOfCalories: Int = getColumnIndexOrThrow(_stmt, "calories")
        val _result: MutableList<FoodLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FoodLogEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_cursorIndexOfDate)
          val _tmpCarbs: Double
          _tmpCarbs = _stmt.getDouble(_cursorIndexOfCarbs)
          val _tmpFats: Double
          _tmpFats = _stmt.getDouble(_cursorIndexOfFats)
          val _tmpProtein: Double
          _tmpProtein = _stmt.getDouble(_cursorIndexOfProtein)
          val _tmpCalories: Double
          _tmpCalories = _stmt.getDouble(_cursorIndexOfCalories)
          _item = FoodLogEntity(_tmpId,_tmpDate,_tmpCarbs,_tmpFats,_tmpProtein,_tmpCalories)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteFoodLogsInRange(startTime: Long, endTime: Long) {
    val _sql: String = "DELETE FROM food_logs WHERE date >= ? AND date <= ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startTime)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endTime)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
