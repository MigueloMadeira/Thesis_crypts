package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command reads the current TPMS_TIME_INFO structure that contains the current setting of Time, Clock, resetCount, and restartCount.
*/
public class TPM2_ReadClock_REQUEST extends TpmStructure
{
    /**
     * This command reads the current TPMS_TIME_INFO structure that contains the current setting of Time, Clock, resetCount, and restartCount.
     */
    public TPM2_ReadClock_REQUEST()
    {
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ReadClock_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ReadClock_REQUEST ret = new TPM2_ReadClock_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ReadClock_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ReadClock_REQUEST ret = new TPM2_ReadClock_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ReadClock_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
    };
    
    
};

//<<<

