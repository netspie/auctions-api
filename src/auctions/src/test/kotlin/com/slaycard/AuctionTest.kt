package com.slaycard

import Auction
import com.slaycard.basic.plus
import com.slaycard.entities.Money
import com.slaycard.entities.UserId
import com.slaycard.entities.events.AuctionPriceOutbidEvent
import junit.framework.TestCase.assertFalse
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertTrue

class AuctionTest {

    val timeNow = LocalDateTime(2023, 2, 28, 0, 0, 0, 0)

    @Test
    fun should_not_be_finished_after_creation() {
        val auction = Auction(timeNow = timeNow)

        val period = DateTimePeriod(hours = 71)
        val newTime = timeNow + period
        assertFalse(auction.isFinished(timeNow + period))
    }

    @Test
    fun should_outbid_if_price_greater_by_5_percent() {
        val auction = Auction(timeNow = timeNow)
        val result = auction.outbid(Money(105), UserId(""), timeNow)

        assertTrue(result.isSuccess)
        assertTrue(auction.events.any {
            val ev = it as? AuctionPriceOutbidEvent ?: return@any false
            ev.auction == auction.id && ev.newPrice == auction.currentPrice
        })
    }

    @Test
    fun should_not_outbid_if_price_not_greater_than_5_percent() {
        val auction = Auction(timeNow = timeNow)
        val result = auction.outbid(Money(104), UserId(""), timeNow)

        assertFalse(result.isSuccess)
        assertFalse(auction.events.any{ it is AuctionPriceOutbidEvent })
    }
}
