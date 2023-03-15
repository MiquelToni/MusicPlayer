import com.mnm.common.models.Command
import com.mnm.common.models.PlayCommand
import com.mnm.common.networking.jsonSerializer
import kotlin.test.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

class PolymorphicSerializationTest {
    private val json = jsonSerializer

    @Test
    fun serializeCommand() {
        val at = 1
        val command: Command = PlayCommand(at = at)

        val encodedMessage = json.encodeToString(command)
        println(encodedMessage)

        when (val decoded = json.decodeFromString<Command>(encodedMessage)) {
            is PlayCommand -> assert(decoded.at == at)
            else -> fail()
        }
    }
}