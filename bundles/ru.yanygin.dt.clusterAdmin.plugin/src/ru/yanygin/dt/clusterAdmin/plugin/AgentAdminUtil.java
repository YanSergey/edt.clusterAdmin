package ru.yanygin.dt.clusterAdmin.plugin;

import java.util.List;
import java.util.UUID;

import com._1c.v8.ibis.admin.AgentAdminException;
import com._1c.v8.ibis.admin.IAgentAdminConnection;
import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfoShort;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.client.IAgentAdminConnector;
import com._1c.v8.ibis.admin.client.IAgentAdminConnectorFactory;

/**
 * Utility class for interaction with the administration server of 1C:Enterprise 
 * server cluster
 */
public final class AgentAdminUtil
{
	private final IAgentAdminConnectorFactory factory;

	private IAgentAdminConnector connector;
	private IAgentAdminConnection connection;

	public AgentAdminUtil(IAgentAdminConnectorFactory factory)
	{
		this.factory = factory;
	}

    /**
	 * Establishes connection with the administration server of 1C:Enterprise 
     * server cluster
	 *
     * @param address server address
     * @param port IP port
     * @param timeout connection timeout (in milliseconds)
     *
     * @throws AgentAdminException in the case of errors.
	 */
	public void connect(String address, int port, long timeout)
	{
        if (connection != null)
        {
            throw new IllegalStateException("The connection is already established.");
        }

	    connector = factory.createConnector(timeout);
	    connection = connector.connect(address, port);
	}

	/**
	 * Checks whether connection to the administration server is established
	 *
	 * @return {@code true} if connected, {@code false} overwise
	 */
	public boolean isConnected()
	{
		return connection != null;
	}

	/**
	 * Terminates connection to the administration server
     *
     * @throws AgentAdminException in the case of errors.
	 */
	public void disconnect()
	{
        if (connection == null)
        {
            throw new IllegalStateException("The connection is not established.");
        }

        try
        {
	        connector.shutdown();
        }
        finally
        {
	        connection = null;
	        connector = null;
        }
	}
	
	/**
	 * Performs cluster authentication
	 * 
	 * @param clusterId cluster ID
	 * @param userName cluster administrator name
	 * @param password cluster administrator password
	 */
	public void authenticateCluster(UUID clusterId, String userName, String password)
	{
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

		connection.authenticate(clusterId, userName, password);
	}

	/**
     * Adds infobase authentication parameters to the context 
     * of the current administration server connection
	 * 
	 * @param clusterId cluster ID
	 * @param userName infobase administrator name
	 * @param password infobase administrator password
	 */
    public void addInfoBaseCredentials(UUID clusterId, String userName,
        String password)
	{
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

		connection.addAuthentication(clusterId, userName, password);
	}
	
    /**
     * Gets the list of cluster descriptions
     *
     * @return list of cluster descriptions
     */
    public List<IClusterInfo> getClusterInfoList()
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        return connection.getClusters();
    }

    /**
     * Gets the list of short descriptions of cluster infobases
     *
     * @param clusterId cluster ID
     * @return list of short descriptions of cluster infobases
     */
    public List<IInfoBaseInfoShort> getInfoBaseShortInfos(UUID clusterId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

    	return connection.getInfoBasesShort(clusterId);
    }

    /**
	 * Gets the full infobase description
	 *
	 * @param clusterId cluster ID
	 * @param infoBaseId infobase ID
	 * @return infobase full infobase description
	 */
	public IInfoBaseInfo getInfoBaseInfo(UUID clusterId, UUID infoBaseId)
	{
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}
		
		return connection.getInfoBaseInfo(clusterId, infoBaseId);
	}

    /**
     * Updates infobase parameters
     *
     * @param clusterId cluster ID
     * @param info infobase parameters
     */
    public void updateInfoBase(UUID clusterId, IInfoBaseInfo info)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}
		
		connection.updateInfoBase(clusterId, info);
    }
	
    /**
     * Terminates all sessions for all infobases in the cluster
     *
     * @param clusterId cluster ID
     */
    public void terminateAllSessions(UUID clusterId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        List<ISessionInfo> sessions = connection.getSessions(clusterId);
        for (ISessionInfo session : sessions)
        {
        	connection.terminateSession(clusterId, session.getSid());
        }
    }
    
    public void terminateAllSessionsOfInfobase(UUID clusterId, UUID infobaseId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        List<ISessionInfo> sessions = connection.getInfoBaseSessions(clusterId, infobaseId);
        for (ISessionInfo session : sessions)
        {
        	connection.terminateSession(clusterId, session.getSid());
        }
    }
}
