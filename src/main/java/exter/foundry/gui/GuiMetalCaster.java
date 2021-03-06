package exter.foundry.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import exter.foundry.container.ContainerMetalCaster;
import exter.foundry.gui.button.GuiButtonRedstoneMode;
import exter.foundry.tileentity.TileEntityMetalCaster;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiMetalCaster extends GuiFoundry
{

  private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("foundry:textures/gui/caster.png");

  private static final int TANK_HEIGHT = 47;
  private static final int TANK_X = 39;
  private static final int TANK_Y = 21;
  
  private static final int PROGRESS_X = 60;
  private static final int PROGRESS_Y = 51;
  private static final int PROGRESS_WIDTH = 22;
  private static final int PROGRESS_HEIGHT = 15;

  private static final int POWER_X = 59;
  private static final int POWER_Y = 21;
  private static final int POWER_WIDTH = 3;
  private static final int POWER_HEIGHT = 16;
  
  
  private static final int TANK_OVERLAY_X = 176;
  private static final int TANK_OVERLAY_Y = 0;

  private static final int PROGRESS_OVERLAY_X = 176;
  private static final int PROGRESS_OVERLAY_Y = 53;

  private static final int POWER_OVERLAY_X = 176;
  private static final int POWER_OVERLAY_Y = 71;
  
  private static final int RSMODE_X = 176 - GuiButtonRedstoneMode.TEXTURE_WIDTH - 4;
  private static final int RSMODE_Y = 4;
  private static final int RSMODE_TEXTURE_X = 176;
  private static final int RSMODE_TEXTURE_Y = 90;

  
  
  private TileEntityMetalCaster te_caster;
  private GuiButtonRedstoneMode button_mode;

  public GuiMetalCaster(TileEntityMetalCaster cs, IInventory player_inv)
  {
    super(new ContainerMetalCaster(cs, player_inv));
    allowUserInput = false;
    ySize = 166;
    te_caster = cs;
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouse_x, int mouse_y)
  {
    super.drawGuiContainerForegroundLayer(mouse_x, mouse_y);

    fontRenderer.drawString("Metal Caster", 5, 6, 0x404040);
    fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
  {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    mc.renderEngine.bindTexture(GUI_TEXTURE);
    int window_x = (width - xSize) / 2;
    int window_y = (height - ySize) / 2;
    drawTexturedModalRect(window_x, window_y, 0, 0, xSize, ySize);

    //Draw progress bar.
    int progress = te_caster.GetProgress() * PROGRESS_WIDTH / TileEntityMetalCaster.CAST_TIME;
    if(progress > 0)
    {
      drawTexturedModalRect(window_x + PROGRESS_X, window_y + PROGRESS_Y, PROGRESS_OVERLAY_X, PROGRESS_OVERLAY_Y, progress, PROGRESS_HEIGHT);
    }

    //Draw stored power bar.
    int power = te_caster.GetStoredPower() * POWER_HEIGHT / te_caster.GetMaxStoredEnergy();
    if(power > 0)
    {
      drawTexturedModalRect(window_x + POWER_X, window_y + POWER_Y + POWER_HEIGHT - power, POWER_OVERLAY_X, POWER_OVERLAY_Y + POWER_HEIGHT - power, POWER_WIDTH, power);
    }
    DisplayTank(window_x, window_y, TANK_X, TANK_Y, TANK_HEIGHT, TANK_OVERLAY_X, TANK_OVERLAY_Y, te_caster.GetTank(0));
  }

  @Override
  public void drawScreen(int mouse_x, int mouse_y, float par3)
  {
    super.drawScreen(mouse_x, mouse_y, par3);
    
    //Draw tool tips.
    
    if(isPointInRegion(TANK_X,TANK_Y,16,TANK_HEIGHT,mouse_x,mouse_y))
    {
      DisplayTankTooltip(mouse_x, mouse_y, te_caster.GetTank(0));
    }

    
    if(isPointInRegion(POWER_X,POWER_Y,POWER_WIDTH,POWER_HEIGHT,mouse_x,mouse_y))
    {
      List<String> list = new ArrayList<String>();
      int power = te_caster.GetStoredPower();
      int  max_power = te_caster.GetMaxStoredEnergy();
      list.add("Energy: " + String.valueOf(power) + "/" + String.valueOf(max_power));
      drawHoveringText(list, mouse_x, mouse_y, fontRenderer);
    }
    if(isPointInRegion(RSMODE_X,RSMODE_Y,GuiButtonRedstoneMode.TEXTURE_WIDTH,GuiButtonRedstoneMode.TEXTURE_HEIGHT,mouse_x,mouse_y))
    {
      List<String> list = new ArrayList<String>();
      switch(te_caster.GetMode())
      {
        case RSMODE_IGNORE:
          list.add("Mode: Ignore Restone");
          break;
        case RSMODE_OFF:
          list.add("Mode: Redstone signal OFF");
          break;
        case RSMODE_ON:
          list.add("Mode: Redstone signal ON");
          break;
        case RSMODE_PULSE:
          list.add("Mode: Redstone pulse");
          break;
      }
      drawHoveringText(list, mouse_x, mouse_y, fontRenderer);
    }
  }

  @Override
  protected ResourceLocation GetGUITexture()
  {
    return GUI_TEXTURE;
  }

  @SuppressWarnings("unchecked")
  @Override 
  public void initGui()
  {
    super.initGui();
    int window_x = (width - xSize) / 2;
    int window_y = (height - ySize) / 2;
    button_mode = new GuiButtonRedstoneMode(1, RSMODE_X + window_x, RSMODE_Y + window_y,GUI_TEXTURE,RSMODE_TEXTURE_X,RSMODE_TEXTURE_Y);
    buttonList.add(button_mode);
  }

  @Override
  protected void actionPerformed(GuiButton button)
  {
    if(button.id == button_mode.id)
    {
      te_caster.SetMode(te_caster.GetMode().Next());
    }
  }
}
