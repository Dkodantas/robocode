package meurobo;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.awt.*;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * MeuRobo - a robot by (your name here)
 */
public class MeuRobo extends AdvancedRobot{
	boolean movingForward; // É definida como true quando setAhead é chamada e vice-versa
	boolean inWall; // É verdade quando robô está perto da parede.

	public void run() {

		setColors();

		// Cada parte do robô move-se livremente dos outros.
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		// Está mais perto do que 50px da parede.
		if (getX() <= 50 || getY() <= 50
				|| getBattleFieldWidth() - getX() <= 50
				|| getBattleFieldHeight() - getY() <= 50) {
			this.inWall = true;
		} else {
			this.inWall = false;
		}

		setAhead(40000); 
		setTurnRadarRight(360); // Scannear inimigo
		this.movingForward = true; // Chamou setAhead

		while (true) {
			// Verifica está perto da parede e se já veiricou positivo, Caso não verificou, inverter a direção e setar flag para verdadeiro.
			if (getX() > 50 && getY() > 50
					&& getBattleFieldWidth() - getX() > 50
					&& getBattleFieldHeight() - getY() > 50
					&& this.inWall == true) {
				this.inWall = false;
			}
			if (getX() <= 50 || getY() <= 50
					|| getBattleFieldWidth() - getX() <= 50
					|| getBattleFieldHeight() - getY() <= 50) {
				if (this.inWall == false) {
					reverseDirection();
					inWall = true;
				}
			}

			// Se o radar parou de girar, procure um inimigo
			if (getRadarTurnRemaining() == 0.0) {
				setTurnRadarRight(360);
			}

			execute(); // executar todas as ações set.
		}
	}

	public void onHitWall(HitWallEvent e) {
		reverseDirection();
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		// Calcular a posição do robô
		double absoluteBearing = getHeading() + e.getBearing();

		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing
				- getGunHeading());
				
		double bearingFromRadar = normalRelativeAngleDegrees(absoluteBearing
				- getRadarHeading());

		// Realizar movimento
		
		if (this.movingForward) {
			setTurnRight(normalRelativeAngleDegrees(e.getBearing() + 80));
		} else {
			setTurnRight(normalRelativeAngleDegrees(e.getBearing() + 100));
		}

		// Se tiver perto atira
		if (Math.abs(bearingFromGun) <= 4) {
			setTurnGunRight(bearingFromGun); 
			setTurnRadarRight(bearingFromRadar); 

			if (getGunHeat() == 0 && getEnergy() > .2) {
				fire(Math.min(
						4.5 - Math.abs(bearingFromGun) / 2 - e.getDistance() / 250, 
						getEnergy() - .1));
			}
		} 
		else {
			setTurnGunRight(bearingFromGun);
			setTurnRadarRight(bearingFromRadar);
		}

		if (bearingFromGun == 0) {
			scan();
		}
	}

	public void onHitRobot(HitRobotEvent e) {
		if (e.isMyFault()) {
			reverseDirection();
		}
	}

	private void setColors() {
		setBodyColor(Color.BLACK);
		setGunColor(Color.BLACK);
		setRadarColor(Color.RED);
		setBulletColor(Color.RED);
		setScanColor(Color.RED);
	}

	
	public void reverseDirection() {
		if (this.movingForward) {
			setBack(40000);
			this.movingForward = false;
		} else {
			setAhead(40000);
			this.movingForward = true;
		}
	}
}
