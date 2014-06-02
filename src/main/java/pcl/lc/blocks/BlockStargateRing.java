package pcl.lc.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pcl.common.base.GenericContainerBlock;
import pcl.common.multiblock.EnumOrientations;
import pcl.common.util.Vector3;
import pcl.lc.LanteaCraft;
import pcl.lc.multiblock.StargateMultiblock;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateRing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStargateRing extends GenericContainerBlock {

	static final int numSubBlocks = 2;
	public static final int subBlockMask = 0x1;
	IIcon topAndBottomTexture;
	IIcon sideTextures[] = new IIcon[numSubBlocks];

	public BlockStargateRing(int id) {
		super(id, Block.blocksList[4].blockMaterial);
		setHardness(50F);
		setResistance(2000F);
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return LanteaCraft.getAssetKey() + ":" + getUnlocalizedName() + "_" + LanteaCraft.getProxy().getRenderMode();
	}

	@Override
	public int getRenderType() {
		if (LanteaCraft.Render.blockStargateRingRenderer != null)
			return LanteaCraft.Render.blockStargateRingRenderer.renderID;
		return -9001;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		topAndBottomTexture = register.registerIcon(LanteaCraft.getAssetKey() + ":" + "stargateBlock_"
				+ LanteaCraft.getProxy().getRenderMode());
		sideTextures[0] = register.registerIcon(LanteaCraft.getAssetKey() + ":" + "stargateRing_"
				+ LanteaCraft.getProxy().getRenderMode());
		sideTextures[1] = register.registerIcon(LanteaCraft.getAssetKey() + ":" + "stargateChevron_"
				+ LanteaCraft.getProxy().getRenderMode());
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta) {
		return true;
	}

	@Override
	public int damageDropped(int data) {
		return data;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float cx,
			float cy, float cz) {
		TileEntityStargateRing te = (TileEntityStargateRing) world.getTileEntity(x, y, z);
		if (te.getAsPart().isMerged()) {
			Vector3 base = te.getAsPart().findHostMultiblock(false).getLocation();
			Block block = Block.blocksList[world.getBlockId(base.floorX(), base.floorY(), base.floorZ())];
			if (block instanceof BlockStargateBase)
				block.onBlockActivated(world, base.floorX(), base.floorY(), base.floorZ(), player, side, cx, cy, cz);
			return true;
		}
		return false;
	}

	@Override
	public IIcon getIcon(int side, int data) {
		if (side <= 1)
			return topAndBottomTexture;
		else
			return sideTextures[data & subBlockMask];
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < numSubBlocks; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		te.hostBlockPlaced();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int data) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		if (te != null) {
			te.flagDirty();
			if (te.getAsPart().findHostMultiblock(false) != null) {
				TileEntity host = te.getAsPart().findHostMultiblock(false).getTileEntity();
				if (host instanceof TileEntityStargateBase)
					((TileEntityStargateBase) host).hostBlockDestroyed();
			}
		}
		super.breakBlock(world, x, y, z, block, data);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityStargateRing();
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess block, int x, int y, int z) {
		TileEntity tileof = block.getTileEntity(x, y, z);
		if (tileof instanceof TileEntityStargateRing) {
			TileEntityStargateRing ring = (TileEntityStargateRing) tileof;
			if (ring.getAsPart() != null && ring.getAsPart().isMerged()) {
				StargateMultiblock master = (StargateMultiblock) ring.getAsPart().findHostMultiblock(false);
				EnumOrientations orientation = master.getOrientation();
				if (orientation != null)
					switch (orientation) {
					case NORTH:
					case SOUTH:
					case NORTH_SOUTH:
						setBlockBounds(0.35f, 0.0f, 0.0f, 0.65f, 1.0f, 1.0f);
						break;
					case EAST:
					case WEST:
					case EAST_WEST:
						setBlockBounds(0.0f, 0.0f, 0.35f, 1.0f, 1.0f, 0.65f);
						break;
					default:
						setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
					}
				else
					setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
			} else
				setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
		} else
			setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

}
