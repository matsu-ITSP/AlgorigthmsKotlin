package encoding.hamming


import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MatrixTest {

    @Test
    fun testInitialize(){
        // 1*1
        createMatrixTest(
            listOf(listOf(Bit(true)))
        )
        // 1*2
        createMatrixTest(
            listOf(listOf(Bit(true), Bit(false)))
        )
        // 2*1
        createMatrixTest(
            listOf(
                listOf(Bit(true)),
                listOf(Bit(false)))
        )
        // 2*2
        createMatrixTest(
            listOf(
                listOf(Bit(true), Bit(true)),
                listOf(Bit(false), Bit(true)))
        )
        // 3*2
        createMatrixTest(
            listOf(
                listOf(Bit(true), Bit(true), Bit(false)),
                listOf(Bit(false), Bit(true), Bit(false)))
        )
    }

    private fun createMatrixTest(ls: List<List<Bit>>){
        val result = Matrix(ls)
        assertEquals(ls, result.contents)
        assertEquals(ls.size, result.column)
        assertEquals(ls[0].size, result.row)
    }

    @Test
    fun testInitializeFail() {
        assertFailsWith<IllegalArgumentException> {
            Matrix(
                listOf(
                    listOf(true, false).map { Bit(it) },
                    listOf(true).map { Bit(it) }
                )
            )
        }
    }

    @Test
    fun testIdentity(){
        assertEquals(
            Matrix(listOf(listOf(Bit(true)))),
            Matrix.createIdentityMatrix(1)
        )
        assertEquals(
            Matrix(listOf(
                listOf(Bit(true), Bit(false)),
                listOf(Bit(false), Bit(true)))
            ),
            Matrix.createIdentityMatrix(2)
        )
        assertEquals(
            Matrix(listOf(
                listOf(Bit(true), Bit(false), Bit(false)),
                listOf(Bit(false), Bit(true), Bit(false)),
                listOf(Bit(false), Bit(false), Bit(true)))
            ),
            Matrix.createIdentityMatrix(3)
        )
    }

    @Test
    fun testTurn(){
        // 1*1
        assertEquals(
            Matrix(listOf(listOf(Bit(true)))),
            Matrix(listOf(listOf(Bit(true)))).turn()
        )
        // 2*2
        assertEquals(
            Matrix.createFromInt(listOf(
                listOf(1,1),
                listOf(0,1)
            )),
            Matrix.createFromInt(listOf(
                listOf(1,0),
                listOf(1,1)
            )).turn()
        )
        // 2*3
        assertEquals(
            Matrix.createFromInt(listOf(
                listOf(1,1),
                listOf(0,0),
                listOf(0,0)
            )),
            Matrix.createFromInt(listOf(
                listOf(1,0,0),
                listOf(1,0,0)
            )).turn()
        )
    }

    @Test
    fun testTimes(){
        //1*1
        assertEquals(
            Matrix(listOf(listOf(Bit(false)))),
            Matrix(listOf(listOf(Bit(true)))) * Matrix(listOf(listOf(Bit(false))))
        )
        //2*2
        assertEquals(
            Matrix.createFromInt(listOf(
                listOf(1,0),
                listOf(0,1)
            )),
            Matrix.createFromInt(listOf(
                listOf(1,0),
                listOf(1,1)
            )) * Matrix.createFromInt(listOf(
                listOf(1,0),
                listOf(1,1)
            ))

        )
        //2*3
        assertEquals(
            Matrix.createFromInt(listOf(
                listOf(1,0,0),
                listOf(0,1,0),
                listOf(0,0,0)
            )),
            Matrix.createFromInt(listOf(
                listOf(1,0),
                listOf(0,1),
                listOf(0,0)
            )) *
            Matrix.createFromInt(listOf(
                listOf(1,0,0),
                listOf(0,1,0)
            ))
        )
    }

    @Test
    fun testConcatenation(){
        // 1*1 + 1*2
        assertEquals(
            Matrix.createFromInt(listOf(
                listOf(1,1,0)
            )),
            Matrix.createFromInt(listOf(
                listOf(1)
            )).concatenation(Matrix.createFromInt(listOf(
                listOf(1,0)
            )))
        )
        // 3*3 + 3*2
        assertEquals(
            Matrix.createFromInt(listOf(
                listOf(1,0,0,1,0),
                listOf(0,1,0,0,0),
                listOf(0,0,0,1,1)
            )),
            Matrix.createFromInt(listOf(
                listOf(1,0,0),
                listOf(0,1,0),
                listOf(0,0,0)
            )).concatenation(Matrix.createFromInt(listOf(
                listOf(1,0),
                listOf(0,0),
                listOf(1,1)
            )))
        )
    }
}