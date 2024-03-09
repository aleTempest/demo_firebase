import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.FileInputStream

fun main() {
    val file = FileInputStream("src/main/resources/animalia-41539-firebase-adminsdk-pthow-03db54ad81.json")
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(file))
        .setDatabaseUrl("https://animalia-41539-default-rtdb.firebaseio.com")
        .build()
    FirebaseApp.initializeApp(options)
    val db = FirestoreClient.getFirestore()
    val future = db.collection("test").get()
    for (doc in future.get()) {
        println(doc)
    }
}