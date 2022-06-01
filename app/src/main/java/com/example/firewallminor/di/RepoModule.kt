import com.example.firewallminor.data.repository.AuthRepository
import org.koin.dsl.module

val repoModule = module {
    single { AuthRepository() }
}
