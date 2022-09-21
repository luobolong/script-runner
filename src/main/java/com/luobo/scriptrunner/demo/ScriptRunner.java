package com.luobo.scriptrunner.demo;

import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ScriptRunner
{
    private static final Logger LOGGER =
            Logger.getLogger(ScriptRunner.class.getName());
    @Value("${ssh.username}")
    private String strUserName;
    @Value("${ssh.connection-ip}")
    private String strConnectionIP;
    @Value("${ssh.connection-port}")
    private int intConnectionPort;
    @Value("${ssh.password}")
    private String strPassword;
    private Session sesConnection;
    @Value("${ssh.timeout}")
    private int intTimeOut;

    @PostConstruct
    public String connect()
    {
        String errorMessage = null;
        try
        {
            JSch jschSSHChannel = new JSch();
            sesConnection = jschSSHChannel.getSession(strUserName,
                    strConnectionIP, intConnectionPort);
            sesConnection.setPassword(strPassword);
            sesConnection.setConfig("StrictHostKeyChecking", "no");
            sesConnection.connect(intTimeOut);
        }
        catch(JSchException jschX)
        {
            errorMessage = jschX.getMessage();
        }

        return errorMessage;
    }

    private String logError(String errorMessage)
    {
        if(errorMessage != null)
        {
            LOGGER.log(Level.SEVERE, "{0}:{1} - {2}",
                    new Object[]{strConnectionIP, intConnectionPort, errorMessage});
        }

        return errorMessage;
    }

    private String logWarning(String warnMessage)
    {
        if(warnMessage != null)
        {
            LOGGER.log(Level.WARNING, "{0}:{1} - {2}",
                    new Object[]{strConnectionIP, intConnectionPort, warnMessage});
        }

        return warnMessage;
    }

    public String sendCommand(String command)
    {
        StringBuilder outputBuffer = new StringBuilder();
        try
        {
            Channel channel = sesConnection.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            InputStream commandOutput = channel.getInputStream();
            channel.connect();
            int readByte = commandOutput.read();

            while(readByte != 0xffffffff)
            {
                outputBuffer.append((char)readByte);
                readByte = commandOutput.read();
            }
            channel.disconnect();
        }
        catch(IOException | JSchException ioX)
        {
            logWarning(ioX.getMessage());
            return null;
        }
        return outputBuffer.toString();
    }

    @PreDestroy
    public void close()
    {
        sesConnection.disconnect();
    }
}