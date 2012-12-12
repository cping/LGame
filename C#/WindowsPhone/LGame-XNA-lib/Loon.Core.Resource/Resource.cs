using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Loon.Core.Resource
{
    public interface Resource : LRelease
    {

        Stream GetInputStream();

        string GetResourceName();

        Uri GetURI();
    }
}
