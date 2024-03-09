import com.google.cloud.firestore.GeoPoint
import org.apache.commons.csv.CSVFormat
import java.io.BufferedReader
import java.io.FileReader
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun main() {
    val csv = BufferedReader(FileReader("src/main/resources/esp_endemicas.csv"))
    val parser = CSVFormat.DEFAULT.withHeader().parse(csv)
    for (record in parser) {
        val sciName = record.get("especie").toString()
        val uuid = getUUID(sciName)
        val fechacolecta = record.get("fechacolecta").toString()
        val longitud = record.get("longitud").toDouble()
        val latitud = record.get("latitud").toDouble()
        val localidad = record.get("localidad").toString()
        val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
        val sightDate = if (fechacolecta.isNotEmpty()) LocalDate.parse(fechacolecta, formater) else null
        val sightDateAsDate = sightDate?.let { Date.from(it.atStartOfDay(ZoneId.systemDefault()).toInstant()) }
        val map = mapOf(
            "id" to UUID.randomUUID(),
            "animal_uuid" to uuid,
            "user_id" to null,
            "map_location" to GeoPoint(latitud,longitud),
            "sight_date" to sightDateAsDate,
            "location" to localidad
        )
        val db = FirebaseConnection.getDB()
        val api = db.collection("sightings").document(map["id"].toString()).set(map)
        println(uuid)
        api.get()
    }
    FirebaseConnection.close()
}