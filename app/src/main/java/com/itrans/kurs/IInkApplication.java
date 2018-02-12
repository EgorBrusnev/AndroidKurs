

package com.itrans.kurs;

import android.app.Application;

import com.itrans.kurs.certificate.MyCertificate;
import com.myscript.iink.Engine;

public class IInkApplication extends Application
{
    private static Engine engine;

    public static synchronized Engine getEngine()
    {
        if (engine == null)
        {
            engine = Engine.create(MyCertificate.getBytes());
        }

        return engine;
    }

}
