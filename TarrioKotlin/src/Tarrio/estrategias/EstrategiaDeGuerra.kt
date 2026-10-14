package Tarrio.estrategias
import robocode.JuniorRobot

sealed interface EstrategiaDeGuerra {

    fun run(robot: JuniorRobot)

    fun onScannedRobot(robot: JuniorRobot){
        robot.bearGunTo(robot.scannedBearing)
        robot.fire(calcularPotencia(robot.scannedDistance.toDouble()))
    }

    fun onHitBullet(robot: JuniorRobot){
        robot.bearGunTo(robot.hitByBulletBearing)
        robot.fire(calcularPotencia(robot.scannedDistance.toDouble()))
    }

    fun onHitWall(robot: JuniorRobot){
        robot.back(10)
    }

    fun calcularPotencia(distancia: Double): Double = if (distancia < 100.0) 3.0 else 2.0

}