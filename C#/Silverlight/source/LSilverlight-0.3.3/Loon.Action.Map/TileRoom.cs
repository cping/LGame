namespace Loon.Action.Map
{
    using System.Collections.Generic;
    using Loon.Utils;
    
    public class TileRoom
    {

        public class RoomLink
        {

            private TileRoom room;

            internal int x, y;

            public RoomLink(TileRoom room_0, int x_1, int y_2)
            {
                this.x = x_1;
                this.y = y_2;
                this.room = room_0;
            }

            public override int GetHashCode()
            {
                return this.room.GetHashCode() + this.x + this.y;
            }

            public override bool Equals(object other)
            {
                TileRoom.RoomLink o = (TileRoom.RoomLink)other;
                return (o.room == this.room) && (this.x == o.x) && (this.y == o.y);
            }

            public int GetX()
            {
                return this.x;
            }

            public int GetY()
            {
                return this.y;
            }
        }

        private int x;

        private int y;

        private int width;

        private int height;

        private Dictionary<TileRoom, RoomLink> connected;

        private List<TileRoom> connectedRooms;

        private bool locked;

        private bool joined;

        public TileRoom(int x_0, int y_1, int width_2, int height_3)
        {
            this.connected = new Dictionary<TileRoom, RoomLink>();
            this.connectedRooms = new List<TileRoom>();
            this.locked = false;
            this.x = x_0;
            this.y = y_1;
            this.width = width_2;
            this.height = height_3;
        }

        public int GetWidth()
        {
            return this.width;
        }

        public int GetHeight()
        {
            return this.height;
        }

        public int GetX()
        {
            return this.x;
        }

        public int GetY()
        {
            return this.y;
        }

        public void SetLock(bool l)
        {
            this.locked = l;
        }

        public bool IsLock()
        {
            return this.locked;
        }

        public int GetDoorX(TileRoom other)
        {
            TileRoom.RoomLink record = (TileRoom.RoomLink)CollectionUtils.Get(this.connected, other);
            return record.x;
        }

        public int GetDoorY(TileRoom other)
        {
            TileRoom.RoomLink record = (TileRoom.RoomLink)CollectionUtils.Get(this.connected, other);
            return record.y;
        }

        public void Connect(TileRoom other, int x_0, int y_1)
        {
            TileRoom.RoomLink record = new TileRoom.RoomLink(other, x_0, y_1);
            if (CollectionUtils.Get(this.connected, other) == null)
            {
                CollectionUtils.Put(this.connected, other, record);
                CollectionUtils.Add(this.connectedRooms, other);
                other.Connect(this, x_0, y_1);
            }
        }

        public bool Contains(int xp, int yp)
        {
            return (xp >= this.x) && (yp >= this.y) && (xp < this.x + this.width)
                    && (yp < this.y + this.height);
        }

        public Dictionary<TileRoom, RoomLink> Connected()
        {
            return this.connected;
        }

        public int GetCenterX()
        {
            return this.x + this.width / 2;
        }

        public int GetCenterY()
        {
            return this.y + this.height / 2;
        }

        public List<TileRoom> ConnectedRooms()
        {
            return this.connectedRooms;
        }

        public TileRoom.RoomLink GetDoor(TileRoom room_0)
        {
            return (TileRoom.RoomLink)CollectionUtils.Get(this.connected, room_0);
        }

        public void Convert(Field2D field, int ins0, int xout)
        {
            for (int xp = 0; xp < this.width; xp++)
            {
                for (int yp = 0; yp < this.height; yp++)
                {
                    if (field.GetType(this.x + xp, this.y + yp) == ins0)
                    {
                        field.SetType(this.x + xp, this.y + yp, xout);
                    }
                }
            }
        }

        public bool IsJoined()
        {
            return this.joined;
        }

        public void SetJoined(bool j)
        {
            this.joined = j;
        }
    }
}
