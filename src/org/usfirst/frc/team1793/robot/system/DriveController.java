package org.usfirst.frc.team1793.robot.system;

import static org.usfirst.frc.team1793.robot.Constants.TURN_SPEED;
import static org.usfirst.frc.team1793.robot.Constants.TURN_THRESHOLD;

import org.usfirst.frc.team1793.robot.Constants;
import org.usfirst.frc.team1793.robot.Robot;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;

public class DriveController extends Controller {
	public static volatile boolean turnFinished;

	public static enum Turn {
		LEFT, RIGHT, DONE
	}

	private Drive drive;

	public DriveController(SpeedController leftMotor, SpeedController rightMotor) {
		super(4);
		drive = new Drive(leftMotor, rightMotor);
	}

	public void arcadeDrive(double speed, double rotation) {
		executor.execute(() -> drive.arcadeDrive(speed, rotation));
	}

	public void drive(double speed) {
		executor.execute(() -> drive.drive(Constants.DRIVE_SPEED * speed));
	}

	public void turnToAngle(double angle) {
		executor.execute(() -> {
			turnFinished = drive.turnToAngle(angle);
		});

	}

	public void turn(double speed) {
		executor.execute(() -> {
			drive.turn(speed);
		});
	}

	public static class Drive extends RobotDrive {

		public Drive(SpeedController leftMotor, SpeedController rightMotor) {
			super(leftMotor, rightMotor);
			leftMotor.setInverted(true);
		}

		public void drive(double speed) {
			setLeftRightMotorOutputs(speed, speed);
		}

		public void turn(double speed) {
			setLeftRightMotorOutputs(speed, -speed);
		}

		public boolean turnToAngle(double angle) {
			Turn turn = angleThreshold(angle);
			switch (turn) {
			case DONE:
				return true;
			case LEFT:
				turn(-TURN_SPEED);
				return false;
			case RIGHT:
				turn(TURN_SPEED);
				return false;
			default:
				return false;
			}
		}

	}

	public static Turn angleThreshold(double angle) {
		double diff = angle - Robot.getInstance().gyro.getAngle();
		boolean currentBigger = Robot.getInstance().gyro.getAngle() > angle;
		if (Math.abs(diff) <= TURN_THRESHOLD) {
			return Turn.DONE;
		} else {
			if (currentBigger) {
				if (diff < 0) {
					return Turn.LEFT;

				} else {
					return Turn.RIGHT;
				}
			} else {
				if (diff < 0) {
					return Turn.RIGHT;

				} else {
					return Turn.LEFT;
				}
			}
		}

	}

}
