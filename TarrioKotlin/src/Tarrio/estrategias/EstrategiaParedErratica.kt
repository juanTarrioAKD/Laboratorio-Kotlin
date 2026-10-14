package Tarrio.estrategias

import robocode.JuniorRobot
import kotlin.random.Random
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min

class EstrategiaParedErratica : EstrategiaDeGuerra {

    companion object {
        private const val MARGEN = 30
        private const val PASO_MIN = 60
        private const val PASO_MAX = 200
        private const val ARCO_MEDIO = 75 // grados a cada lado del centro del barrido
        private const val INCREMENTO_BARRIDO = 20 // grados que avanza el cañón en cada paso
    }

    // Dirección perpendicular a la pared, apuntando hacia adentro del campo.
    private var anguloBase = 0
    // Posición actual del barrido, relativa a anguloBase (-ARCO_MEDIO .. +ARCO_MEDIO).
    private var offsetBarrido = 0
    // Sentido del barrido: +1 o -1.
    private var sentidoBarrido = 1

    override fun run(robot: JuniorRobot) {
        val distIzquierda = robot.robotX.toDouble()
        val distDerecha = (robot.fieldWidth - robot.robotX).toDouble()
        val distAbajo = robot.robotY.toDouble()
        val distArriba = (robot.fieldHeight - robot.robotY).toDouble()

        val minima = min(min(distIzquierda, distDerecha), min(distAbajo, distArriba))
        val paredHorizontal = minima == distAbajo || minima == distArriba

        val destinoInicial = when (minima) {
            distIzquierda -> {
                anguloBase = 90 // pared izquierda -> el campo está hacia el este
                Punto(MARGEN, robot.robotY)
            }
            distDerecha -> {
                anguloBase = 270 // pared derecha -> el campo está hacia el oeste
                Punto(robot.fieldWidth - MARGEN, robot.robotY)
            }
            distAbajo -> {
                anguloBase = 0 // pared inferior -> el campo está hacia el norte
                Punto(robot.robotX, MARGEN)
            }
            else -> {
                anguloBase = 180 // pared superior -> el campo está hacia el sur
                Punto(robot.robotX, robot.fieldHeight - MARGEN)
            }
        }

        irA(robot, destinoInicial)

        val (extremo1, extremo2) = if (paredHorizontal) {
            val y = destinoInicial.y
            Punto(MARGEN, y) to Punto(robot.fieldWidth - MARGEN, y)
        } else {
            val x = destinoInicial.x
            Punto(x, MARGEN) to Punto(x, robot.fieldHeight - MARGEN)
        }

        patrullarErratico(robot, extremo1, extremo2, paredHorizontal)
    }

    private fun patrullarErratico(robot: JuniorRobot, extremo1: Punto, extremo2: Punto, horizontal: Boolean) {
        // Ya no orientamos el cuerpo en base al cañón: el cuerpo mira hacia
        // extremo2 para poder usar ahead()/back(), pero el cañón se maneja
        // aparte con barrerCanion(), de forma independiente.
        orientarHacia(robot, extremo2)

        while (true) {
            val pasos = Random.nextInt(PASO_MIN, PASO_MAX + 1)
            var avanzar = Random.nextBoolean()

            val posActual = (if (horizontal) robot.robotX else robot.robotY).toDouble()
            val limiteMin = (if (horizontal) extremo1.x else extremo1.y).toDouble()
            val limiteMax = (if (horizontal) extremo2.x else extremo2.y).toDouble()

            if (avanzar && posActual + pasos > limiteMax) {
                avanzar = false
            } else if (!avanzar && posActual - pasos < limiteMin) {
                avanzar = true
            }

            if (avanzar) {
                robot.ahead(pasos)
            } else {
                robot.back(pasos)
            }

            barrerCanion(robot)
        }
    }

    /**
     * Hace oscilar el cañón dentro de un arco de +/- ARCO_MEDIO grados,
     * centrado en anguloBase (la dirección que apunta hacia el campo,
     * lejos de la pared). Cuando llega a un extremo del arco, invierte
     * el sentido, generando un barrido tipo "radar de vigilancia" que
     * nunca pierde tiempo mirando hacia la pared.
     */
    private fun barrerCanion(robot: JuniorRobot) {
        offsetBarrido += INCREMENTO_BARRIDO * sentidoBarrido

        if (offsetBarrido > ARCO_MEDIO) {
            offsetBarrido = ARCO_MEDIO
            sentidoBarrido = -1
        } else if (offsetBarrido < -ARCO_MEDIO) {
            offsetBarrido = -ARCO_MEDIO
            sentidoBarrido = 1
        }

        val anguloObjetivo = (anguloBase + offsetBarrido + 360) % 360
        robot.turnGunTo(anguloObjetivo)
    }

    /** Gira el robot para quedar orientado hacia el destino, sin moverse. */
    private fun orientarHacia(robot: JuniorRobot, destino: Punto) {
        val dx = (destino.x - robot.robotX).toDouble()
        val dy = (destino.y - robot.robotY).toDouble()
        var angulo = Math.round(Math.toDegrees(atan2(dx, dy))).toInt()
        if (angulo < 0) {
            angulo += 360
        }
        robot.turnTo(angulo)
    }

    /** Gira hacia el destino y avanza en línea recta hasta él. */
    private fun irA(robot: JuniorRobot, destino: Punto) {
        orientarHacia(robot, destino)
        val dx = (destino.x - robot.robotX).toDouble()
        val dy = (destino.y - robot.robotY).toDouble()
        val distancia = Math.round(hypot(dx, dy)).toInt()
        robot.ahead(distancia)
    }
}
