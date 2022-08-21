package com.funnco.fcmessenger

import com.funnco.fcmessenger.utils.HashingUtil
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID
import org.assertj.core.api.Assertions.*


@SpringBootTest
class FcMessengerApplicationTests {

    @Test
    fun contextLoads() {
    }

    @Test
    fun passwordHashingTest(){
        val testUUID = UUID.fromString("e820800a-b4c4-4f05-9ba8-c49e7a147abe")
        val password1 = "QWERTY"
        val password2 = "HarderPassword!"

        val hashedPassword1 = HashingUtil.hashPassword(password1, testUUID)
        val hashedPassword2 = HashingUtil.hashPassword(password2, testUUID)

        assertThat(hashedPassword1).isNotNull
        assertThat(hashedPassword2).isNotNull

        assert(hashedPassword1 != hashedPassword2)

        assert(hashedPassword1 == "b094535c01711bd4ded79a100d97176e")
        assert(hashedPassword2 != "b094535c01711bd4ded79a100d97176e")
    }
}
