package Tarrio

import Tarrio.estrategias.EstrategiaDeGuerra
import Tarrio.estrategias.EstrategiaCampera
import Tarrio.estrategias.EstrategiaParedErratica
import robocode.JuniorRobot


class Tarrio : JuniorRobot(){
    private val estrategia = EstrategiaParedErratica()

    override fun run (){
        setColors(orange,blue,white,yellow,black)
        estrategia.run(this)
    }

    override fun onScannedRobot() {
        estrategia.onScannedRobot(this)
    }

    override fun onHitByBullet() {
        estrategia.onHitBullet(this)
    }

    override fun onHitWall() {
        estrategia.onHitWall(this)
    }
}