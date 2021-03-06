package lc.common.impl.drivers;

import lc.api.components.IntegrationType;
import lc.api.drivers.IPowerDriver;
import lc.api.jit.DeviceDrivers;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

/**
 * Buildcraft power driver.
 *
 * @author AfterLifeLochie
 *
 */
@DeviceDrivers.DriverProvider(type = IntegrationType.POWER)
public class DriverBCPower implements IPowerDriver, IPowerEmitter, IPowerReceptor {

	PowerHandler driverBCPower_receiver;

	@Override
	public double getStoredEnergy() {
		// Auto-generated method stub
		return 0;
	}

	@Override
	public double getEnergyCapacity() {
		// Auto-generated method stub
		return 0;
	}

	@Override
	public double acceptEnergy(double quantity, boolean simulated) {
		// Auto-generated method stub
		return 0;
	}

	@Override
	public double pullEnergy(double quantity, boolean simulated) {
		// Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canInterface(ForgeDirection direction) {
		// Auto-generated method stub
		return true;
	}

	@Override
	public World getWorld() {
		// Auto-generated method stub
		return null;
	}

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		if (driverBCPower_receiver == null)
			driverBCPower_receiver = new PowerHandler(this, Type.PIPE);
		return driverBCPower_receiver.getPowerReceiver();
	}

	@Override
	public void doWork(PowerHandler workProvider) {
		double avail = workProvider.useEnergy(0, Double.MAX_VALUE, true);
		acceptEnergy(avail, false);
	}

	@Override
	public boolean canEmitPowerFrom(ForgeDirection side) {
		return canInterface(side);
	}

}
