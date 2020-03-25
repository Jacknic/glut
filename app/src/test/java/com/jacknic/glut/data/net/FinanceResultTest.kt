package com.jacknic.glut.data.net

import com.jacknic.glut.data.model.Financial
import com.jacknic.glut.data.util.fromJSON
import org.junit.Assert
import org.junit.Test
import java.io.File

/**
 * 财务信息解析
 *
 * @author Jacknic
 */
class FinanceResultTest {

    @Test
    fun testFinance() {
        val json = File("./src/test/res/finance.json").readText()
        val result = fromJSON<FinanceResult<Financial>>(json)
        println(result)
        Assert.assertNotNull(result)
        result?.apply {
            Assert.assertTrue(success)
            Assert.assertTrue(state == 200)
            Assert.assertTrue(data != null)
            Assert.assertEquals("316206666666", data?.sid)
        }
    }
}