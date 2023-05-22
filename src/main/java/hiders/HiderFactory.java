package hiders;


import utils.Channel;

import java.util.List;


public class HiderFactory
{
    private List<Channel> usedChannels = List.of(Channel.RED, Channel.GREEN, Channel.BLUE);
    private int qInByte = 2;
    private double sglEnergy = 0.7;
    private int areaForAvgVal = 3;
    private int bruyndBlockSize = 8;
    private int kochBlockSize = 8;
    private int kochEps = 12;


    public Hider createHider(HiderType type)
    {
        return switch (type)
                {
            case SIMPLE -> new SimpleHider(usedChannels, qInByte);

            case CUTTER -> new CutterHider(sglEnergy, areaForAvgVal);

            case BRUYNDONCKX -> new BruyndonckxHider(bruyndBlockSize);

            case KOCHZHAO -> new KochZhaoHider(kochBlockSize, kochEps);
        };
    }

    public void setUsedChannels(List<Channel> usedChannels) { this.usedChannels = usedChannels; }

    public void setqInByte(int qInByte) { this.qInByte = qInByte; }

    public void setSglEnergy(double sglEnergy) { this.sglEnergy = sglEnergy; }

    public void setAreaForAvgVal(int areaForAvgVal) { this.areaForAvgVal = areaForAvgVal; }

    public void setBruyndBlockSize(int bruyndBlockSize) { this.bruyndBlockSize = bruyndBlockSize; }

    public void setKochBlockSize(int kochBlockSize) { this.kochBlockSize = kochBlockSize; }

    public void setKochEps(int kochEps) { this.kochEps = kochEps; }

    public List<Channel> getUsedChannels() { return usedChannels; }

    public int getQInByte() { return qInByte; }

    public double getSglEnergy() { return sglEnergy; }

    public int getAreaForAvgVal() { return areaForAvgVal; }

    public int getBruyndBlockSize() { return bruyndBlockSize; }

    public int getKochBlockSize() { return kochBlockSize; }

    public int getKochEps() { return kochEps; }
}
