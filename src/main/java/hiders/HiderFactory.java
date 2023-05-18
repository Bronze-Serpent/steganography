package hiders;


import utils.Channel;

import java.util.List;


public class HiderFactory
{
    private static final List<Channel> USED_CHANNELS = List.of(Channel.RED, Channel.GREEN, Channel.BLUE);
    private static final int Q_IN_BYTE = 2;
    private static final double SGL_ENERGY = 0.7;
    private static final int AREA_FOR_AVG_VAL = 3;
    private static final int BLOCK_SIZE = 8;

    public Hider createHider(HiderType type)
    {
        return switch (type)
                {
            case SIMPLE -> new SimpleHider(USED_CHANNELS, Q_IN_BYTE);

            case CUTTER -> new CutterHider(SGL_ENERGY, AREA_FOR_AVG_VAL);

            case BRUYNDONCKX -> new BruyndonckxHider(BLOCK_SIZE);
        };
    }
}
