/**
 * Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package buildcraft.builders;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.BuildCraftBuilders;
import buildcraft.api.events.BlockInteractionEvent;
import buildcraft.api.tools.IToolWrench;
import buildcraft.core.BlockBuildCraft;
import buildcraft.core.CreativeTabBuildCraft;
import buildcraft.core.GuiIds;
import buildcraft.core.utils.Utils;

public class BlockArchitect extends BlockBuildCraft {
	IIcon blockTextureTop;
	IIcon blockTextureSide;
	IIcon blockTextureFront;

	public BlockArchitect() {
		super(Material.iron, CreativeTabBuildCraft.BLOCKS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileArchitect();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int par6, float par7,
			float par8, float par9) {

		// Drop through if the player is sneaking
		if (entityplayer.isSneaking()) {
			return false;
		}

		BlockInteractionEvent event = new BlockInteractionEvent(entityplayer, this);
		FMLCommonHandler.instance().bus().post(event);
		if (event.isCanceled()) {
			return false;
		}

		Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem() : null;
		if (equipped instanceof IToolWrench && ((IToolWrench) equipped).canWrench(entityplayer, x, y, z)) {

			int meta = world.getBlockMetadata(x, y, z);

			switch (ForgeDirection.values()[meta]) {
			case WEST:
				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.SOUTH.ordinal(), 0);
				break;
			case EAST:
				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.NORTH.ordinal(), 0);
				break;
			case NORTH:
				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.WEST.ordinal(), 0);
				break;
			case SOUTH:
			default:
				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.EAST.ordinal(), 0);
				break;
			}

			world.markBlockForUpdate(x, y, z);
			((IToolWrench) equipped).wrenchUsed(entityplayer, x, y, z);
			return true;
		} else if (equipped instanceof ItemConstructionMarker) {
			ItemConstructionMarker.link(entityplayer.getCurrentEquippedItem(), world, x, y, z);

			return true;
		} else {

			if (!world.isRemote) {
				entityplayer.openGui(BuildCraftBuilders.instance, GuiIds.ARCHITECT_TABLE, world, x, y, z);
			}
			return true;

		}
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int par6) {
		Utils.preDestroyBlock(world, i, j, k);

		super.breakBlock(world, i, j, k, block, par6);
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
		super.onBlockPlacedBy(world, i, j, k, entityliving, stack);

		ForgeDirection orientation = Utils.get2dOrientation(entityliving);

		world.setBlockMetadataWithNotify(i, j, k, orientation.getOpposite().ordinal(), 1);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int i, int j) {
		if (j == 0 && i == 3) {
			return blockTextureFront;
		}

		if (i == j) {
			return blockTextureFront;
		}

		switch (i) {
			case 1:
				return blockTextureTop;
			default:
				return blockTextureSide;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		blockTextureTop = par1IconRegister.registerIcon("buildcraft:architect_top");
		blockTextureSide = par1IconRegister.registerIcon("buildcraft:architect_side");
		blockTextureFront = par1IconRegister.registerIcon("buildcraft:architect_front");
	}
}
