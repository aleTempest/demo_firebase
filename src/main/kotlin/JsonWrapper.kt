import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.json.JSONObject
import java.io.*
import java.util.UUID
import com.google.firebase.cloud.FirestoreClient
import java.util.StringTokenizer

class JsonWrapper(
    val jsonStr: String
) {
    private lateinit var obj: JSONObject

    init {
        obj = JSONObject(File(jsonStr).readText())
    }

    fun getKeys(): List<String> {
        val keys = mutableListOf<String>()
        for (entry in obj.keys()) {
            keys.add(entry)
        }
        return keys
    }

    fun getValue(key: String, field: String): Any? {
        if (obj.get(key) != JSONObject.NULL) {
            if (obj.getJSONObject(key).has(field) && obj.getJSONObject(key).get(field) != null) {
                return obj.getJSONObject(key).get(field)
            }
        }
        return null
    }

    fun getMap(key: String, fields: List<String>): Map<String,Any?> {
        val map = mutableMapOf<String,Any?>()
        for (field in fields) {
            map.put(field,getValue(key,field))
        }
        return map
    }
}

class Animal(
    val sciName: String,
    val commName: String,
    val addNames: List<String>,
    val img: String
) : Comparable<Animal> {
    val uuid = UUID.randomUUID()

    fun toMap(): Map<String,Any> {
        return mapOf(
            "uuid" to uuid,
            "sci_name" to sciName,
            "common_name" to commName,
            "add_names" to addNames,
            "img_url" to img
        )
    }

    // metodo de ordenamiento para los objeto de esta clase, no tiene realmente ninguna relevancia pero
    // por razones de conveniencia, el ordenamiento se hace desde aqui antes de subir a la base de datos
    // se utiliza -1 para revertir el orden de la lista

    // aftermath, al parecer a firebase le vale el orden de la lista - yo a la 1pm
    // aftermath del aftermath, tiene sentido - yo a las 10pm
    override fun compareTo(other: Animal): Int {
        // Mientras más nombres comunes tenga, más prioridad
        if (this.addNames.size > other.addNames.size) return -1
        return 0
    }
}


class FirebaseConnection {
    companion object {
        private var initialized = false

        fun initialize() {
            if (!initialized) {
                val file = FileInputStream("src/main/resources/animalia-41539-firebase-adminsdk-pthow-03db54ad81.json")
                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(file))
                    .setDatabaseUrl("https://animalia-41539-default-rtdb.firebaseio.com")
                    .build()
                FirebaseApp.initializeApp(options)
                initialized = true
            }
        }

        fun close() {
            if (initialized) {
                FirebaseApp.getInstance().delete()
                initialized = false
            }
        }

        fun getDB(): Firestore {
            initialize()
            return FirestoreClient.getFirestore()
        }
    }
}

fun getUUID(str: String): UUID? {
    return convertUUIDStringToUUID(getDocId(str))
}

fun convertUUIDStringToUUID(uuidString: String): UUID? {
    val regex = Regex("""\{(mostSignificantBits=(-?\d+)), (leastSignificantBits=(-?\d+))\}""")
    val matchResult = regex.find(uuidString)

    if (matchResult != null) {
        val mostSignificantBits = matchResult.groups[2]?.value?.toLong()
        val leastSignificantBits = matchResult.groups[4]?.value?.toLong()

        if (mostSignificantBits != null && leastSignificantBits != null) {
            return UUID(mostSignificantBits, leastSignificantBits)
        } else {
            println("Invalid UUID string format.")
        }
    } else {
        println("UUID string not in the expected format.")
    }
    return null
}

fun getDocId(str: String): String {
    val ref = FirebaseConnection.getDB().collection("animalia").whereEqualTo("sci_name", str)
    val query = ref.get()
    var docStr = "" // knowing that the query will always return 1 value
    for (doc in query.get().documents) {
        docStr = doc.get("uuid").toString()
        println(docStr)
    }
    return docStr
}


/*fun uploadToFirebase(list: List<Animal>) {
// fun uploadToFirebase() {
    val db = getDB()
    for (item in list) {
        val api = db.collection("animalia").document(item.sciName).set(item.toMap())
        api.get()
    }
}*/

/* fun main() {
    val jsonFile = "src/main/resources/esp_data_api.json"
    val jsonWrapper = JsonWrapper(jsonFile)
    val fields = listOf("nombre_comun_principal","NombreCompleto","nombres_comunes_adicionales","foto_principal")
    val names = jsonWrapper.getKeys()
    val animals = mutableListOf<Animal>()
    for (name in names) {
        val jsonMap = jsonWrapper.getMap(name,fields)
        val commName = jsonMap["nombre_comun_principal"].toString()
        val addNames = jsonMap["nombres_comunes_adicionales"].toString().split(",")
        val img = jsonMap["foto_principal"].toString()
        val auxAnimal = Animal(name,commName,addNames,img)
        animals.add(auxAnimal)
    }
    print(convertUUIDStringToUUID(getDocId("Anoptichthys antrobius")))
}
*/