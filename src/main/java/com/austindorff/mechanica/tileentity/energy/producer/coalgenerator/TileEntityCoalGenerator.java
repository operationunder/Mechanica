package com.austindorff.mechanica.tileentity.energy.producer.coalgenerator;

import com.austindorff.mechanica.energy.EnumResistance;
import com.austindorff.mechanica.energy.EnumVoltage;
import com.austindorff.mechanica.energy.IEnergyStorage;
import com.austindorff.mechanica.network.packet.energy.PacketEnergy;
import com.austindorff.mechanica.tileentity.energy.producer.TileEntityEnergyProducerBase;
import com.austindorff.mechanica.tileentity.energy.producer.TileEntityEnergyProducerBaseInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TileEntityCoalGenerator extends TileEntityEnergyProducerBaseInventory implements IInventory, ITickable, IEnergyStorage {

	//produces 8 energy per tick
	//produces 1024 energy per coal 
	
	private int burnTime = 128;
	private int internalStorage = 0;
	private int internalStorageLimit = 4096;
	private int currentBurnTime = 0;
	private boolean isBurning = false;
	
	public TileEntityCoalGenerator() {
		super("Coal Generator", 1);
	}
	
	public boolean burning() {
		return this.isBurning;
	}

	@Override
	public EnumVoltage getVoltageProducedEnum() {
		return EnumVoltage.TIER_ONE;
	}

	@Override
	public EnumResistance getResistanceEnum() {
		return EnumResistance.TIER_TWO;
	}

	@Override
	public boolean isCorrectTileEntity(TileEntity tile) {
		return tile instanceof TileEntityCoalGenerator;
	}

	@Override
	public void sendEnergyPacket(PacketEnergy packet) {
		this.getEnergyNetwork().injectPacket(packet);
	}

	@Override
	public void updateBlockState(BlockPos coords) {
		
	}

	@Override
	public void update() {
		if (this.canBurn() && this.currentBurnTime == 0) {
			this.getInventory()[0].stackSize--;
			if (this.getInventory()[0].stackSize == 0) {
				this.getInventory()[0] = null;
			}
			this.currentBurnTime++;
			this.isBurning = true;
			this.markDirty();
		}
		if (this.currentBurnTime != 0) {
			currentBurnTime++;
			if (this.canInjectEnergyPacketIntoNetwork()) {
				this.getEnergyNetwork().injectPacket(getEnergyPacket());
			} else {
				this.internalStorage += this.getCurrentProduced();
			}
			if (this.currentBurnTime >= this.burnTime) {
				this.currentBurnTime = 0;
				this.isBurning = false;
			}
			this.markDirty();
		}
	}
	
	public boolean canBurn() {
		return this.getInventory()[0] != null && ((this.internalStorage + 1024 <= this.internalStorageLimit) || canInjectEnergyPacketIntoNetwork());
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return isItemFuel(stack);
	}
	
	public boolean isItemFuel(ItemStack stack) {
		return stack.getItem() == Items.COAL;
	}

	@Override
	public int getField(int id) {
		switch(id) {
			case 0: {
				return this.isBurning ? 1 : 0;
			}
			case 1: {	
				return this.currentBurnTime;
			}
			case 2: {
				return this.internalStorage;
			}
			case 3: {
				return this.burnTime;
			}
			case 4: {
				return this.internalStorageLimit;
			}
			default: {
				return 0;
			}
		}
	}

	@Override
	public void setField(int id, int value) {
		switch(id) {
			case 0: {
				this.isBurning = value == 1 ? true : false;
				break;
			}
			case 1: {
				this.currentBurnTime = value;
			}
			case 2: {
				this.internalStorage = value;
			}
			case 3: {
				
			}
			case 4: {
				
			}
		}
	}

	@Override
	public int getFieldCount() {
		return 5;
	}

	@Override
	public boolean isFull() {
		return this.internalStorage == this.internalStorageLimit;
	}

	@Override
	public boolean isEmpty() {
		return this.internalStorage == 0;
	}

	@Override
	public boolean isPartiallyFull() {
		return !this.isEmpty() && !this.isFull();
	}

	@Override
	public EnumVoltage getVoltageEnum() {
		return getVoltageProducedEnum();
	}

	@Override
	public boolean canRecieveEnergyFromNetworkInDirection(EnumFacing facing) {
		return false;
	}

	@Override
	public int getEnergyOutput() {
		return getCurrentProduced();
	}

}