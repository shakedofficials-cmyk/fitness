package com.recompos.app.export

import com.recompos.app.data.local.*
import org.json.JSONArray
import org.json.JSONObject

data class ImportSnapshot(
    val bodyweights: List<BodyweightLogEntity>,
    val waists: List<WaistLogEntity>,
    val nutrition: List<NutritionLogEntity>,
    val sleep: List<SleepLogEntity>,
    val digestion: List<DigestionLogEntity>,
    val steps: List<StepsLogEntity>,
    val cardio: List<CardioLogEntity>,
    val sets: List<SetLogEntity>
)

class ExportImportManager {
    fun exportJson(
        bodyweights: List<BodyweightLogEntity>,
        waists: List<WaistLogEntity>,
        nutrition: List<NutritionLogEntity>,
        sleep: List<SleepLogEntity>,
        digestion: List<DigestionLogEntity>,
        steps: List<StepsLogEntity>,
        cardio: List<CardioLogEntity>,
        sets: List<SetLogEntity>
    ): String = JSONObject()
        .put("schema", 1)
        .put("bodyweights", JSONArray(bodyweights.map { JSONObject().put("date", it.dateEpochDay).put("weight", it.weight).put("notes", it.notes) }))
        .put("waists", JSONArray(waists.map { JSONObject().put("date", it.dateEpochDay).put("waist", it.waist).put("notes", it.notes) }))
        .put("nutrition", JSONArray(nutrition.map { JSONObject().put("date", it.dateEpochDay).put("calories", it.calories).put("protein", it.protein).put("carbs", it.carbs).put("fat", it.fat).put("notes", it.notes) }))
        .put("sleep", JSONArray(sleep.map { JSONObject().put("date", it.dateEpochDay).put("hours", it.hours).put("quality", it.quality).put("notes", it.notes) }))
        .put("digestion", JSONArray(digestion.map { JSONObject().put("date", it.dateEpochDay).put("score", it.score).put("reflux", it.reflux).put("bloating", it.bloating).put("triggers", it.triggerFoods).put("notes", it.notes) }))
        .put("steps", JSONArray(steps.map { JSONObject().put("date", it.dateEpochDay).put("steps", it.steps).put("notes", it.notes) }))
        .put("cardio", JSONArray(cardio.map { JSONObject().put("date", it.dateEpochDay).put("modality", it.modality).put("duration", it.durationMinutes).put("intensity", it.intensity).put("notes", it.notes) }))
        .put("sets", JSONArray(sets.map { JSONObject().put("exerciseSessionId", it.exerciseSessionId).put("set", it.setNumber).put("weight", it.weight).put("reps", it.reps).put("rir", it.rir).put("pain", it.painScore).put("warmup", it.isWarmup).put("drop", it.isDropSet).put("notes", it.notes).put("completedAt", it.completedAtMillis) }))
        .toString(2)

    fun exportCsv(title: String, rows: List<List<String>>): String {
        val escaped = rows.joinToString("\n") { row ->
            row.joinToString(",") { cell -> "\"" + cell.replace("\"", "\"\"") + "\"" }
        }
        return "# $title\n$escaped"
    }

    fun validateImport(json: String): Boolean =
        runCatching { JSONObject(json).getInt("schema") == 1 }.getOrDefault(false)

    fun parseImport(json: String): ImportSnapshot {
        val root = JSONObject(json)
        require(root.getInt("schema") == 1) { "Unsupported RecompOS import schema." }
        return ImportSnapshot(
            bodyweights = root.optJSONArray("bodyweights").mapObjects {
                BodyweightLogEntity(dateEpochDay = it.getLong("date"), weight = it.getDouble("weight"), notes = it.optString("notes"))
            },
            waists = root.optJSONArray("waists").mapObjects {
                WaistLogEntity(dateEpochDay = it.getLong("date"), waist = it.getDouble("waist"), notes = it.optString("notes"))
            },
            nutrition = root.optJSONArray("nutrition").mapObjects {
                NutritionLogEntity(
                    dateEpochDay = it.getLong("date"),
                    calories = it.getInt("calories"),
                    protein = it.getInt("protein"),
                    carbs = it.getInt("carbs"),
                    fat = it.getInt("fat"),
                    notes = it.optString("notes")
                )
            },
            sleep = root.optJSONArray("sleep").mapObjects {
                SleepLogEntity(dateEpochDay = it.getLong("date"), hours = it.getDouble("hours"), quality = it.getInt("quality"), notes = it.optString("notes"))
            },
            digestion = root.optJSONArray("digestion").mapObjects {
                DigestionLogEntity(
                    dateEpochDay = it.getLong("date"),
                    score = it.getInt("score"),
                    reflux = it.optBoolean("reflux"),
                    bloating = it.optBoolean("bloating"),
                    triggerFoods = it.optString("triggers"),
                    notes = it.optString("notes")
                )
            },
            steps = root.optJSONArray("steps").mapObjects {
                StepsLogEntity(dateEpochDay = it.getLong("date"), steps = it.getInt("steps"), notes = it.optString("notes"))
            },
            cardio = root.optJSONArray("cardio").mapObjects {
                CardioLogEntity(
                    dateEpochDay = it.getLong("date"),
                    modality = it.optString("modality"),
                    durationMinutes = it.getInt("duration"),
                    intensity = it.optString("intensity"),
                    notes = it.optString("notes")
                )
            },
            sets = root.optJSONArray("sets").mapObjects {
                SetLogEntity(
                    exerciseSessionId = it.optLong("exerciseSessionId"),
                    setNumber = it.getInt("set"),
                    weight = it.getDouble("weight"),
                    reps = it.getInt("reps"),
                    rir = it.getInt("rir"),
                    painScore = it.getInt("pain"),
                    isWarmup = it.optBoolean("warmup"),
                    isDropSet = it.optBoolean("drop"),
                    notes = it.optString("notes"),
                    completedAtMillis = it.getLong("completedAt")
                )
            }
        )
    }

    private inline fun <T> JSONArray?.mapObjects(mapper: (JSONObject) -> T): List<T> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) add(mapper(getJSONObject(index)))
        }
    }
}
