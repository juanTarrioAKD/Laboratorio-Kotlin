package Tarrio.estrategias

import robocode.JuniorRobot
import kotlin.math.atan2
import kotlin.math.hypot

public class EstrategiaCampera : EstrategiaDeGuerra {

    companion object {
        private const val MARGEN = 50
    }

    override fun run(robot: JuniorRobot) {
        // 1\. Las 4 esquinas del campo con su respectivo
        val esquinas = listOf(
            Punto(MARGEN, MARGEN),
            Punto(MARGEN, robot.fieldHeight - MARGEN),
            Punto(robot.fieldWidth - MARGEN, MARGEN),
            Punto(robot.fieldWidth - MARGEN, robot.fieldHeight - MARGEN)
        )

        // 2\. Búsqueda de la esquina más cercana
        var distanciaMinima = Double.MAX_VALUE

        val esquinaElegida = esquinas.minByOrNull { esquina ->
            hypot((esquina.x - robot.robotX).toDouble(),
                  (esquina.y - robot.robotY).toDouble())
        } ?: esquinas[0]

        // 3\. Cálculo del ángulo hacia la esquina elegida
        val dx = (esquinaElegida.x - robot.robotX).toDouble()
        val dy = (esquinaElegida.y - robot.robotY).toDouble()

        // Math.atan2 devuelve radianes -&gt; Math.toDegrees lo pasa a grados sexagesimales
        var angulo = Math.toDegrees(atan2(dx, dy)).toInt()

        // Ajuste al rango de 0 a 360 grados de Robocode
        if (angulo < 0) { angulo += 360 }

        // 4\. Girar y trasladarse a la esquina
        robot.turnTo(angulo)
        val distanciaFinal = hypot(dx, dy).toInt()
        robot.ahead(distanciaFinal)

        // 5\. Bucle de vigilancia en la esquina: gira el cañón 360° continuamente
        while (true) { robot.turnGunRight(360) }
    }
}