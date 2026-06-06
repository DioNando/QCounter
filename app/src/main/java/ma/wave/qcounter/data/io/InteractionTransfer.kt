package ma.wave.qcounter.data.io

import ma.wave.qcounter.data.local.InteractionEntity
import ma.wave.qcounter.data.model.AnswerType
import org.json.JSONArray
import org.json.JSONObject

/**
 * (Dé)sérialisation de l'historique pour l'import/export. Format JSON simple et stable :
 * seuls le type et l'horodatage sont conservés (les id sont réattribués à l'import).
 */
object InteractionTransfer {

    private const val VERSION = 1

    /** Sérialise les interactions en JSON lisible. */
    fun encode(items: List<InteractionEntity>): String {
        val array = JSONArray()
        for (item in items) {
            array.put(
                JSONObject()
                    .put("type", item.type.name)
                    .put("timestamp", item.timestamp),
            )
        }
        return JSONObject()
            .put("version", VERSION)
            .put("app", "QCounter")
            .put("interactions", array)
            .toString(2)
    }

    /**
     * Parse un export. Tolérant : ignore les entrées invalides (type inconnu, horodatage manquant).
     * Retourne des couples (type, horodatage) prêts à être dédupliqués puis insérés.
     */
    fun decode(text: String): List<Pair<AnswerType, Long>> {
        val root = JSONObject(text)
        val array = root.optJSONArray("interactions") ?: return emptyList()
        val result = ArrayList<Pair<AnswerType, Long>>(array.length())
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val typeName = obj.optString("type", null) ?: continue
            val type = runCatching { AnswerType.valueOf(typeName) }.getOrNull() ?: continue
            val timestamp = obj.optLong("timestamp", -1L)
            if (timestamp <= 0L) continue
            result.add(type to timestamp)
        }
        return result
    }
}
