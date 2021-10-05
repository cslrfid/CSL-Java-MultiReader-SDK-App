package Netfinder;

import Netfinder.Structures.DeviceInformation;

/**
 *
 */
class NetDisplay {
    private final int MAX_ENTRIES = 256;
    private int m_selectedcell = 0;
    private int m_numcells = 0;
    private DeviceInformation[] m_ptr_array = new DeviceInformation[MAX_ENTRIES];

    public int GetNumCells()
    {
        return m_numcells;
    }

    public DeviceInformation GetEntry(int index)
    {
        if (m_ptr_array.length > index)
            return m_ptr_array[index];
        return null;
    }


    public boolean GetMACAddress(byte[] mac_ptr)
    {
        if (!IsCellSelected())
        {
            return false;
        }
        if (mac_ptr == null || mac_ptr.length != 6)
            mac_ptr = new byte[6];
        // Copy MAC address
        System.arraycopy(m_ptr_array[m_selectedcell].mac, 0, mac_ptr, 0, 6);
        return true;
    }
    public boolean GetMACAddress(int cell_number, byte[] mac_ptr)
    {
        if (cell_number >= m_numcells)
        {
            return false;
        }

        if (mac_ptr == null || mac_ptr.length != 6)
            mac_ptr = new byte[6];

        // Copy MAC address
        System.arraycopy(m_ptr_array[cell_number].mac, 0, mac_ptr, 0, 6);
        return true;
    }

    public boolean IsCellSelected()
    {
        return (m_selectedcell != -1);
    }

    public void RemoveAllEntries()
    {
        int i;

        // Delete all entries
        for (i = 0; i < MAX_ENTRIES; i++)
        {
            if (m_ptr_array[i] != null)
            {
                m_ptr_array[i] = null;
            }
        }

        // Update number of cells
        m_numcells = 0;
        m_selectedcell = -1;
    }

    public boolean AddEntry(DeviceInformation pEntry)
    {
        int i;
        DeviceInformation pNewEntry;

        // Find the next available entry
        for (i = 0; i < MAX_ENTRIES; i++)
        {
            if (m_ptr_array[i] == null)
            {
                break;
            }
        }

        // If no entries could be found
        if (i == MAX_ENTRIES)
        {
            return false;
        }

        //-----------------------------
        // Add the entry at location i
        //-----------------------------

        // Create new entry
        pNewEntry = new DeviceInformation();

        // Copy the entry data
        pNewEntry.mode = pEntry.mode;

        pNewEntry.time_on_powered = pEntry.time_on_powered;

        pNewEntry.time_on_network = pEntry.time_on_network;

        //CopyMemory(pNewEntry.mac, pEntry.mac, 6);
        //CopyMemory(pNewEntry.ip, pEntry.ip, 4);
        pNewEntry.mac = (byte[])pEntry.mac.clone();
        pNewEntry.ip = (byte[])pEntry.ip.clone();

        pNewEntry.port = pEntry.port;

        //CopyMemory(pNewEntry.subnet, pEntry.subnet, 4);
        //CopyMemory(pNewEntry.gateway, pEntry.gateway, 4);
        //pNewEntry.device_name = (byte[])pEntry.serverip.Clone();
        pNewEntry.DHCP = pEntry.DHCP;
        pNewEntry.dhcp_retry = pEntry.dhcp_retry;

        pNewEntry.device_name = pEntry.device_name;
        pNewEntry.description = pEntry.description;
        pNewEntry.version = pEntry.version;

        // Store address in global array
        m_ptr_array[i] = pNewEntry;

        // Increment valid cell count
        m_numcells++;

        return true;
    }
}
