package edu.njit.solarcar.electrical.motorLog.can;

/**
 * Represents a method to parse an incoming CAN frame.
 * This will be called automatically in the background
 * by the thread that checks for CAN traffic, whenever
 * a new frame comes in.
 * @author Duemmer
 *
 */
@FunctionalInterface
public interface FrameProcessor {
	public void processFrame(CANFrame frame);
}
