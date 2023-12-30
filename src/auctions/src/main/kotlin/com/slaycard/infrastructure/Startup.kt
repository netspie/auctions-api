package com.slaycard.infrastructure

import Auction
import AuctionId
import com.slaycard.entities.shared.Money
import com.slaycard.api.plugins.configureMonitoring
import com.slaycard.api.plugins.configureRouting
import com.slaycard.entities.roots.AuctionRepository
import com.slaycard.useCases.OutbidAuctionCommandHandler
import com.slaycard.useCases.GetAuctionQueryHandler
import com.slaycard.useCases.GetAuctionsQueryHandler
import com.slaycard.basic.domain.DomainEvent
import com.slaycard.entities.events.AuctionCancelledEvent
import com.slaycard.entities.events.AuctionFinishedEvent
import com.slaycard.entities.events.AuctionPriceOutbidEvent
import com.slaycard.entities.events.AuctionStartedEvent
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.module.dsl.scopedOf
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureApp() {
    FirebaseAdmin.init()
    configureFirebaseAuth()

    install(Koin) {
        slf4jLogger()
        modules(auctionsModule)
    }

    install(ContentNegotiation) {
        val format = Json {
            serializersModule = SerializersModule {
                polymorphic(DomainEvent::class) {
                    subclass(AuctionStartedEvent::class)
                    subclass(AuctionCancelledEvent::class)
                    subclass(AuctionFinishedEvent::class)
                    subclass(AuctionPriceOutbidEvent::class)
                }
            }
        }
        register(ContentType.Application.Json, KotlinxSerializationConverter(format))
    }

    configureMonitoring()
    configureRouting()

    Database.connect(
        "jdbc:postgresql://localhost:5432/sports_db",
        user="", password="")

    transaction {

    }
}

val auctionsModule = module {
    single<AuctionRepository> {
        val repo = InMemoryRepository<Auction, AuctionId>()
        repo.add(Auction(auctionItemName = "Uriziel's Sword", startingPrice = Money(100)))
        repo
    }

    scope<RequestScope> {
        scopedOf(::CreateAuctionCommandHandler)
        scopedOf(::GetAuctionQueryHandler)
        scopedOf(::GetAuctionsQueryHandler)
        scopedOf(::OutbidAuctionCommandHandler)
    }
}

class RequestScope: KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }
}
