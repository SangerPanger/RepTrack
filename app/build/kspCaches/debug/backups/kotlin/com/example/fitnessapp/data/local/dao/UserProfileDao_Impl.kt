package com.example.fitnessapp.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.fitnessapp.`data`.local.database.Converters
import com.example.fitnessapp.`data`.local.entity.UserProfileEntity
import com.example.fitnessapp.domain.model.Sex
import com.example.fitnessapp.domain.model.TrainingGoal
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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

  private val __converters: Converters = Converters()
  init {
    this.__db = __db
    this.__insertAdapterOfUserProfileEntity = object : EntityInsertAdapter<UserProfileEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `user_profile` (`id`,`age`,`sex`,`heightCm`,`bodyWeightKg`,`trainingExperienceMonths`,`detrainingWeeks`,`isReturningLifter`,`averageProteinGramsPerDay`,`averageCaloriesPerDay`,`estimatedTdee`,`fatGramsPerDay`,`fatPercentCalories`,`goal`,`useSettingsForNutrition`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UserProfileEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.age.toLong())
        val _tmp: String = __converters.fromSex(entity.sex)
        statement.bindText(3, _tmp)
        statement.bindDouble(4, entity.heightCm)
        statement.bindDouble(5, entity.bodyWeightKg)
        statement.bindLong(6, entity.trainingExperienceMonths.toLong())
        statement.bindLong(7, entity.detrainingWeeks.toLong())
        val _tmp_1: Int = if (entity.isReturningLifter) 1 else 0
        statement.bindLong(8, _tmp_1.toLong())
        statement.bindDouble(9, entity.averageProteinGramsPerDay)
        statement.bindDouble(10, entity.averageCaloriesPerDay)
        statement.bindDouble(11, entity.estimatedTdee)
        statement.bindDouble(12, entity.fatGramsPerDay)
        statement.bindDouble(13, entity.fatPercentCalories)
        val _tmp_2: String = __converters.fromTrainingGoal(entity.goal)
        statement.bindText(14, _tmp_2)
        val _tmp_3: Int = if (entity.useSettingsForNutrition) 1 else 0
        statement.bindLong(15, _tmp_3.toLong())
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
        val _cursorIndexOfSex: Int = getColumnIndexOrThrow(_stmt, "sex")
        val _cursorIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _cursorIndexOfBodyWeightKg: Int = getColumnIndexOrThrow(_stmt, "bodyWeightKg")
        val _cursorIndexOfTrainingExperienceMonths: Int = getColumnIndexOrThrow(_stmt,
            "trainingExperienceMonths")
        val _cursorIndexOfDetrainingWeeks: Int = getColumnIndexOrThrow(_stmt, "detrainingWeeks")
        val _cursorIndexOfIsReturningLifter: Int = getColumnIndexOrThrow(_stmt, "isReturningLifter")
        val _cursorIndexOfAverageProteinGramsPerDay: Int = getColumnIndexOrThrow(_stmt,
            "averageProteinGramsPerDay")
        val _cursorIndexOfAverageCaloriesPerDay: Int = getColumnIndexOrThrow(_stmt,
            "averageCaloriesPerDay")
        val _cursorIndexOfEstimatedTdee: Int = getColumnIndexOrThrow(_stmt, "estimatedTdee")
        val _cursorIndexOfFatGramsPerDay: Int = getColumnIndexOrThrow(_stmt, "fatGramsPerDay")
        val _cursorIndexOfFatPercentCalories: Int = getColumnIndexOrThrow(_stmt,
            "fatPercentCalories")
        val _cursorIndexOfGoal: Int = getColumnIndexOrThrow(_stmt, "goal")
        val _cursorIndexOfUseSettingsForNutrition: Int = getColumnIndexOrThrow(_stmt,
            "useSettingsForNutrition")
        val _result: UserProfileEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpAge: Int
          _tmpAge = _stmt.getLong(_cursorIndexOfAge).toInt()
          val _tmpSex: Sex
          val _tmp: String
          _tmp = _stmt.getText(_cursorIndexOfSex)
          _tmpSex = __converters.toSex(_tmp)
          val _tmpHeightCm: Double
          _tmpHeightCm = _stmt.getDouble(_cursorIndexOfHeightCm)
          val _tmpBodyWeightKg: Double
          _tmpBodyWeightKg = _stmt.getDouble(_cursorIndexOfBodyWeightKg)
          val _tmpTrainingExperienceMonths: Int
          _tmpTrainingExperienceMonths =
              _stmt.getLong(_cursorIndexOfTrainingExperienceMonths).toInt()
          val _tmpDetrainingWeeks: Int
          _tmpDetrainingWeeks = _stmt.getLong(_cursorIndexOfDetrainingWeeks).toInt()
          val _tmpIsReturningLifter: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_cursorIndexOfIsReturningLifter).toInt()
          _tmpIsReturningLifter = _tmp_1 != 0
          val _tmpAverageProteinGramsPerDay: Double
          _tmpAverageProteinGramsPerDay = _stmt.getDouble(_cursorIndexOfAverageProteinGramsPerDay)
          val _tmpAverageCaloriesPerDay: Double
          _tmpAverageCaloriesPerDay = _stmt.getDouble(_cursorIndexOfAverageCaloriesPerDay)
          val _tmpEstimatedTdee: Double
          _tmpEstimatedTdee = _stmt.getDouble(_cursorIndexOfEstimatedTdee)
          val _tmpFatGramsPerDay: Double
          _tmpFatGramsPerDay = _stmt.getDouble(_cursorIndexOfFatGramsPerDay)
          val _tmpFatPercentCalories: Double
          _tmpFatPercentCalories = _stmt.getDouble(_cursorIndexOfFatPercentCalories)
          val _tmpGoal: TrainingGoal
          val _tmp_2: String
          _tmp_2 = _stmt.getText(_cursorIndexOfGoal)
          _tmpGoal = __converters.toTrainingGoal(_tmp_2)
          val _tmpUseSettingsForNutrition: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_cursorIndexOfUseSettingsForNutrition).toInt()
          _tmpUseSettingsForNutrition = _tmp_3 != 0
          _result =
              UserProfileEntity(_tmpId,_tmpAge,_tmpSex,_tmpHeightCm,_tmpBodyWeightKg,_tmpTrainingExperienceMonths,_tmpDetrainingWeeks,_tmpIsReturningLifter,_tmpAverageProteinGramsPerDay,_tmpAverageCaloriesPerDay,_tmpEstimatedTdee,_tmpFatGramsPerDay,_tmpFatPercentCalories,_tmpGoal,_tmpUseSettingsForNutrition)
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
