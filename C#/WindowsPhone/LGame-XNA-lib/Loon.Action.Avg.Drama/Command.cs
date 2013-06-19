#region LGame License
/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email£ºjavachenpeng@yahoo.com
 * @version 0.3.3
 */
#endregion
namespace Loon.Action.Avg.Drama
{
    using System;
    using System.IO;
    using System.Collections.Generic;
    using System.Text;
    using System.Collections;
    using System.Runtime.CompilerServices;
    using Loon.Utils;
    using Loon.Core;
    using Loon.Utils.Collection;
    using Loon.Java;
    using Loon.Core.Resource;
    using Loon.Utils.Debugging;
    using Loon.Core.Store;

    public class Command : Conversion, LRelease
    {

        private static Dictionary<object, object> scriptLazy;

        private static Dictionary<object, object> scriptContext;

        private static ArrayMap functions;

        private static Dictionary<object, object> setEnvironmentList;

        private static ArrayMap conditionEnvironmentList;

        private StringBuilder readBuffer;

        private string cacheCommandName;

        private bool flaging = false;

        private bool ifing = false;

        private bool functioning = false;

        private bool esleflag = false;

        private bool esleover = false;

        private bool backIfBool = false;

        private bool isClose;

        private string executeCommand;

        private string nowPosFlagName;

        private bool addCommand;

        private bool isInnerCommand;

        private bool isRead;

        private bool isCall;

        private bool isCache;

        private bool if_bool;

        private bool elseif_bool;

        private Command innerCommand;

        private IList temps;

        private IList printTags;

        private IList randTags;

        private int scriptSize;

        private int offsetPos;

        private string[] scriptList;

        private string scriptName;

        public Command(string fileName)
        {
            CreateCache(false);
            FormatCommand(fileName);
        }

        public Command(Stream ins)
        {
            CreateCache(false);
            FormatCommand(ins);
        }

        public Command(string fileName, string[] res)
        {
            CreateCache(false);
            FormatCommand("function", res);
        }

        public static void CreateCache(bool free)
        {
            if (free)
            {
                if (scriptContext == null)
                {
                    scriptContext = new Dictionary<object, object>(1000);
                }
                else
                {
                    scriptContext.Clear();
                }
                if (functions == null)
                {
                    functions = new ArrayMap(20);
                }
                else
                {
                    functions.Clear();
                }
                if (setEnvironmentList == null)
                {
                    setEnvironmentList = new Dictionary<object, object>(20);
                }
                else
                {
                    setEnvironmentList.Clear();
                }
                if (conditionEnvironmentList == null)
                {
                    conditionEnvironmentList = new ArrayMap(30);
                }
                else
                {
                    conditionEnvironmentList.Clear();
                }
            }
            else
            {
                if (scriptContext == null)
                {
                    scriptContext = new Dictionary<object, object>(1000);
                }
                if (functions == null)
                {
                    functions = new ArrayMap(20);
                }
                if (setEnvironmentList == null)
                {
                    setEnvironmentList = new Dictionary<object, object>(20);
                }
                if (conditionEnvironmentList == null)
                {
                    conditionEnvironmentList = new ArrayMap(30);
                }
            }
        }

        public void FormatCommand(string fileName)
        {
            FormatCommand(fileName, Command.IncludeFile(fileName));
        }

        public void FormatCommand(Stream ins)
        {
            FormatCommand("temp" + ins.GetHashCode(), Command.IncludeFile(ins));
        }

        public void FormatCommand(string name, string[] res)
        {
            if (!"function".Equals(name, StringComparison.InvariantCultureIgnoreCase))
            {
                if (functions != null)
                {
                    functions.Clear();
                }
            }
            if (conditionEnvironmentList != null)
            {
                conditionEnvironmentList.Clear();
            }
            if (setEnvironmentList != null)
            {
                CollectionUtils.Put(setEnvironmentList, V_SELECT_KEY, "-1");
            }
            if (readBuffer == null)
            {
                readBuffer = new StringBuilder(256);
            }
            else
            {
                readBuffer.Remove(0, readBuffer.Length - (0));
            }
            this.scriptName = name;
            this.scriptList = res;
            this.scriptSize = res.Length;
            this.offsetPos = 0;
            this.flaging = false;
            this.ifing = false;
            this.isCache = true;
            this.esleflag = false;
            this.backIfBool = false;
            this.functioning = false;
            this.esleover = false;
            this.backIfBool = false;
            this.addCommand = false;
            this.isInnerCommand = false;
            this.isRead = false;
            this.isCall = false;
            this.isCache = false;
            this.if_bool = false;
            this.elseif_bool = false;
        }

        private bool SetupIF(string commandString, string nowPosFlagName,
                Dictionary<object, object> setEnvironmentList, ArrayMap conditionEnvironmentList)
        {
            bool result = false;
            conditionEnvironmentList.Put(nowPosFlagName, (bool)(false));
            try
            {
                IList temps = CommandSplit(commandString);
                int size = temps.Count;
                object valueA = null;
                object valueB = null;
                string condition = null;
                if (size <= 4)
                {
                    valueA = temps[1];
                    valueB = temps[3];
                    valueA = (CollectionUtils.Get(setEnvironmentList, valueA) == null) ? valueA
                            : CollectionUtils.Get(setEnvironmentList, valueA);
                    valueB = (CollectionUtils.Get(setEnvironmentList, valueB) == null) ? valueB
                            : CollectionUtils.Get(setEnvironmentList, valueB);
                    condition = (string)temps[2];
                }
                else
                {
                    int count = 0;
                    StringBuilder sbr = new StringBuilder();
                    for (IEnumerator it = temps.GetEnumerator(); it.MoveNext(); )
                    {
                        string res = (string)it.Current;
                        if (count > 0)
                        {
                            if (!IsCondition(res))
                            {
                                sbr.Append(res);
                            }
                            else
                            {
                                valueA = sbr.ToString();
                                valueA = exp.Parse(valueA).ToString();
                                sbr.Remove(0, sbr.Length - (0));
                                condition = res;
                            }
                        }
                        count++;
                    }
                    valueB = sbr.ToString();
                }

                if (!MathUtils.IsNan((string)valueB))
                {
                    try
                    {
                        valueB = exp.Parse(valueB);
                    }
                    catch (Exception)
                    {
                    }
                }

                if (valueA == null || valueB == null)
                {
                    conditionEnvironmentList
                            .Put(nowPosFlagName, (bool)(false));
                }

                if ("==".Equals(condition))
                {
                    conditionEnvironmentList.Put(nowPosFlagName, (bool)(result = valueA.ToString().Equals(valueB.ToString())));
                }
                else if ("!=".Equals(condition))
                {
                    conditionEnvironmentList.Put(nowPosFlagName, (bool)(result = !valueA.ToString().Equals(valueB.ToString())));
                }
                else if (">".Equals(condition))
                {
                    float numberA = Single.Parse(valueA.ToString(), JavaRuntime.NumberFormat);
                    float numberB = Single.Parse(valueB.ToString(), JavaRuntime.NumberFormat);
                    conditionEnvironmentList.Put(nowPosFlagName, (bool)(result = numberA > numberB));
                }
                else if ("<".Equals(condition))
                {
                    float numberA = Single.Parse(valueA.ToString(), JavaRuntime.NumberFormat);
                    float numberB = Single.Parse(valueB.ToString(), JavaRuntime.NumberFormat);
                    conditionEnvironmentList.Put(nowPosFlagName, (bool)(result = numberA < numberB));
                }
                else if (">=".Equals(condition))
                {
                    float numberA = Single.Parse(valueA.ToString(), JavaRuntime.NumberFormat);
                    float numberB = Single.Parse(valueB.ToString(), JavaRuntime.NumberFormat);
                    conditionEnvironmentList.Put(nowPosFlagName, (bool)(result = numberA >= numberB));
                }
                else if ("<=".Equals(condition))
                {
                    float numberA = Single.Parse(valueA.ToString(), JavaRuntime.NumberFormat);
                    float numberB = Single.Parse(valueB.ToString(), JavaRuntime.NumberFormat);
                    conditionEnvironmentList.Put(nowPosFlagName, (bool)(result = numberA <= numberB));
                }
            }
            catch (Exception ex)
            {
                Log.Exception(ex);
            }
            return result;
        }

        public void OpenCache()
        {
            isCache = true;
        }

        public void CloseCache()
        {
            isCache = false;
        }

        public string NowCacheOffsetName(string cmd)
        {
            return (scriptName + FLAG + offsetPos + FLAG + cmd).ToLower();
        }

        public static void ResetCache()
        {
            if (scriptContext != null)
            {
                scriptContext.Clear();
            }
        }

        public bool IsRead()
        {
            return isRead;
        }

        public void SetRead(bool isRead)
        {
            this.isRead = isRead;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public string[] GetReads()
        {
            string result = readBuffer.ToString();
            result = result.Replace(SELECTS_TAG, "");
            return StringUtils.Split(result, FLAG);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public string GetRead(int index)
        {
            try
            {
                return GetReads()[index];
            }
            catch (Exception ex)
            {
                Log.Exception(ex);
                return null;
            }
        }

        public static string GetNameTag(string messages, string startString,
                string endString)
        {
            IList results = GetNameTags(messages, startString, endString);
            return (results == null || results.Count == 0) ? null
                    : (string)results[0];
        }

        public static IList GetNameTags(string messages, string startString,
                string endString)
        {
            return Command.GetNameTags(messages.ToCharArray(),
                    startString.ToCharArray(), endString.ToCharArray());
        }

        public static IList GetNameTags(char[] messages, char[] startString,
                char[] endString)
        {
            int dlength = messages.Length;
            int slength = startString.Length;
            int elength = endString.Length;
            IList tagList = new List<object>(10);
            bool lookup = false;
            int lookupStartIndex = 0;
            int lookupEndIndex = 0;
            int length;
            StringBuilder sbr = new StringBuilder(100);
            for (int i = 0; i < dlength; i++)
            {
                char tag = messages[i];
                if (tag == startString[lookupStartIndex])
                {
                    lookupStartIndex++;
                }
                if (lookupStartIndex == slength)
                {
                    lookupStartIndex = 0;
                    lookup = true;
                }
                if (lookup)
                {
                    sbr.Append(tag);
                }
                if (tag == endString[lookupEndIndex])
                {
                    lookupEndIndex++;
                }
                if (lookupEndIndex == elength)
                {
                    lookupEndIndex = 0;
                    lookup = false;
                    length = sbr.Length;
                    if (length > 0)
                    {
                        CollectionUtils.Add(tagList, sbr.ToString(1, sbr.Length - elength - 1));
                        sbr.Remove(0, length - (0));
                    }
                }
            }
            return tagList;
        }

        public void Select(int type)
        {
            if (innerCommand != null)
            {
                innerCommand.SetVariable(V_SELECT_KEY, type.ToString());
            }
            SetVariable(V_SELECT_KEY, type.ToString());
        }

        public string GetSelect()
        {
            return (string)GetVariable(V_SELECT_KEY);
        }

        public void SetVariable(string key, object value_ren)
        {
            CollectionUtils.Put(setEnvironmentList, key, value_ren);
        }

        public void SetVariables(Dictionary<object, object> vars)
        {
            CollectionUtils.PutAll(setEnvironmentList, vars);
        }

        public Dictionary<object, object> GetVariables()
        {
            return setEnvironmentList;
        }

        public object GetVariable(string key)
        {
            return CollectionUtils.Get(setEnvironmentList, key);
        }

        public void RemoveVariable(string key)
        {
            CollectionUtils.Remove(setEnvironmentList, key);
        }

        public bool Next()
        {
            return (offsetPos < scriptSize);
        }

        public bool GotoIndex(int offset)
        {
            bool result = offset < scriptSize && offset > 0
                    && offset != offsetPos;
            if (result)
            {
                offsetPos = offset;
            }
            return result;
        }

        public int GetIndex()
        {
            return offsetPos;
        }

        public IList BatchToList()
        {
            IList reslist = new List<object>(scriptSize);
            for (; Next(); )
            {
                string execute = DoExecute();
                if (execute != null)
                {
                    CollectionUtils.Add(reslist, execute);
                }
            }
            return reslist;
        }

        public string BatchToString()
        {
            StringBuilder resString = new StringBuilder(scriptSize * 10);
            for (; Next(); )
            {
                string execute = DoExecute();
                if (execute != null)
                {
                    resString.Append(execute);
                    resString.Append('\n');
                }
            }
            return resString.ToString();
        }

        private void SetupSET(string cmd)
        {
            if (cmd.StartsWith(SET_TAG))
            {
                IList temps = CommandSplit(cmd);
                int len = temps.Count;
                string result = null;
                if (len == 4)
                {
                    result = temps[3].ToString();
                }
                else if (len > 4)
                {
                    StringBuilder sbr = new StringBuilder(len);
                    for (int i = 3; i < temps.Count; i++)
                    {
                        sbr.Append(temps[i]);
                    }
                    result = sbr.ToString();
                }

                if (result != null)
                {
                    foreach (KeyValuePair<object, object> temp in setEnvironmentList)
                    {
                        if (!(StringUtils.StartsWith(result,'"') && StringUtils.EndsWith(result,'"')))
                        {
                            result = StringUtils.ReplaceMatch(result,
                                    (string)temp.Key, temp.Value
                                            .ToString());
                        }
                    }
                    if (StringUtils.StartsWith(result,'"') && StringUtils.EndsWith(result,'"'))
                    {
                        CollectionUtils.Put(setEnvironmentList, temps[1], result.Substring(1, (result.Length - 1) - (1)));
                    }
                    else if (StringUtils.IsChinaLanguage(result)
                          || StringUtils.IsEnglishAndNumeric(result))
                    {
                        CollectionUtils.Put(setEnvironmentList, temps[1], result);
                    }
                    else
                    {
                        CollectionUtils.Put(setEnvironmentList, temps[1], exp.Parse(result));
                    }
                }
                addCommand = false;
            }

        }

        private void SetupRandom(string cmd)
        {
            if (cmd.IndexOf(RAND_TAG) != -1)
            {
                randTags = Command.GetNameTags(cmd, RAND_TAG + BRACKET_LEFT_TAG,
                        BRACKET_RIGHT_TAG);
                if (randTags != null)
                {
                    for (IEnumerator it = randTags.GetEnumerator(); it.MoveNext(); )
                    {
                        string key = (string)it.Current;
                        object value_ren = CollectionUtils.Get(setEnvironmentList, key);
                        if (value_ren != null)
                        {
                            cmd = StringUtils
                                    .ReplaceMatch(cmd, (RAND_TAG + BRACKET_LEFT_TAG
                                            + key + BRACKET_RIGHT_TAG),
                                            value_ren.ToString());
                        }
                        else if (MathUtils.IsNan(key))
                        {
                            cmd = StringUtils
                                    .ReplaceMatch(
                                            cmd,
                                            (RAND_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG)
                                                 ,
                                            LSystem.random.Next(Int32.Parse(key)).ToString());
                        }
                        else
                        {
                            cmd = StringUtils
                                    .ReplaceMatch(cmd, (RAND_TAG + BRACKET_LEFT_TAG
                                            + key + BRACKET_RIGHT_TAG),
                                            LSystem.random.Next().ToString());
                        }
                    }
                }
            }
        }

        private void InnerCallTrue()
        {
            isCall = true;
            isInnerCommand = true;
        }

        private void InnerCallFalse()
        {
            isCall = false;
            isInnerCommand = false;
            innerCommand = null;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public string DoExecute()
        {
            if (isClose)
            {
                return null;
            }
            this.executeCommand = null;
            this.addCommand = true;
            this.isInnerCommand = (innerCommand != null);
            this.if_bool = false;
            this.elseif_bool = false;

            try
            {
                if (isInnerCommand && isCall)
                {
                    SetVariables(innerCommand.GetVariables());
                    if (innerCommand.Next())
                    {
                        return innerCommand.DoExecute();
                    }
                    else
                    {
                        InnerCallFalse();
                        return executeCommand;
                    }
                }
                else if (isInnerCommand && !isCall)
                {
                    SetVariables(innerCommand.GetVariables());
                    if (innerCommand.Next())
                    {
                        return innerCommand.DoExecute();
                    }
                    else
                    {
                        innerCommand = null;
                        isInnerCommand = false;
                        return executeCommand;
                    }
                }

                nowPosFlagName = offsetPos.ToString();
                int length = conditionEnvironmentList.Size();
                if (length > 0)
                {
                    object ifResult = conditionEnvironmentList.Get(length - 1);
                    if (ifResult != null)
                    {
                        backIfBool = (bool)(((Boolean)ifResult));
                    }
                }

                if (scriptList == null)
                {
                    ResetCache();
                    return executeCommand;
                }
                else if (scriptList.Length - 1 < offsetPos)
                {
                    ResetCache();
                    return executeCommand;
                }

                string cmd = scriptList[offsetPos];

                if (cmd.StartsWith(RESET_CACHE_TAG))
                {
                    ResetCache();
                    return executeCommand;
                }

                if (isCache)
                {

                    cacheCommandName = NowCacheOffsetName(cmd);

                    object cache = CollectionUtils.Get(scriptContext, cacheCommandName);
                    if (cache != null)
                    {
                        return (string)cache;
                    }
                }

                if (flaging)
                {
                    flaging = !(cmd.StartsWith(FLAG_LS_E_TAG) || cmd
                            .EndsWith(FLAG_LS_E_TAG));
                    return executeCommand;
                }

                if (!flaging)
                {
                    if (cmd.StartsWith(FLAG_LS_B_TAG)
                            && !cmd.EndsWith(FLAG_LS_E_TAG))
                    {
                        flaging = true;
                        return executeCommand;
                    }
                    else if (cmd.StartsWith(FLAG_LS_B_TAG)
                          && cmd.EndsWith(FLAG_LS_E_TAG))
                    {
                        return executeCommand;
                    }
                }

                SetupRandom(cmd);

                SetupSET(cmd);

                if (cmd.EndsWith(END_TAG))
                {
                    functioning = false;
                    return executeCommand;
                }

                if (cmd.StartsWith(BEGIN_TAG))
                {
                    temps = CommandSplit(cmd);
                    if (temps.Count == 2)
                    {
                        functioning = true;
                        functions.Put(temps[1], new string[0]);
                        return executeCommand;
                    }
                }

                if (functioning)
                {
                    int size = functions.Size() - 1;
                    string[] function = (string[])functions.Get(size);
                    int index = function.Length;
                    function = (string[])CollectionUtils.Expand(function, 1);
                    function[index] = cmd;
                    functions.Set(size, function);
                    return executeCommand;
                }

                if (((!esleflag && !ifing) || (esleflag && ifing))
                        && cmd.StartsWith(CALL_TAG) && !isCall)
                {
                    temps = CommandSplit(cmd);
                    if (temps.Count == 2)
                    {
                        string functionName = (string)temps[1];
                        string[] funs = (string[])functions.Get(functionName);
                        if (funs != null)
                        {
                            innerCommand = new Command(scriptName + FLAG
                                    + functionName, funs);
                            innerCommand.CloseCache();
                            innerCommand.SetVariables(GetVariables());
                            InnerCallTrue();
                            return null;
                        }
                    }
                }

                if (!if_bool && !elseif_bool)
                {
                    if_bool = cmd.StartsWith(IF_TAG);
                    elseif_bool = cmd.StartsWith(ELSE_TAG);

                }

                if (if_bool)
                {
                    esleover = esleflag = SetupIF(cmd, nowPosFlagName,
                            setEnvironmentList, conditionEnvironmentList);
                    addCommand = false;
                    ifing = true;
                }
                else if (elseif_bool)
                {
                    string[] value_ren = StringUtils.Split(cmd, " ");
                    if (!backIfBool && !esleflag)
                    {
                        if (value_ren.Length > 1 && IF_TAG.Equals(value_ren[1]))
                        {
                            esleover = esleflag = SetupIF(
                                    cmd.Replace(ELSE_TAG, "").Trim(),
                                    nowPosFlagName, setEnvironmentList,
                                    conditionEnvironmentList);
                            addCommand = false;

                        }
                        else if (value_ren.Length == 1 && ELSE_TAG.Equals(value_ren[0]))
                        {
                            if (!esleover)
                            {
                                esleover = esleflag = SetupIF("if 1==1",
                                        nowPosFlagName, setEnvironmentList,
                                        conditionEnvironmentList);
                                addCommand = false;
                            }
                        }
                    }
                    else
                    {
                        esleflag = false;
                        addCommand = false;
                        conditionEnvironmentList.Put(nowPosFlagName, (bool)(false));

                    }
                }

                if (cmd.StartsWith(IF_END_TAG))
                {
                    conditionEnvironmentList.Clear();
                    backIfBool = false;
                    addCommand = false;
                    ifing = false;
                    if_bool = false;
                    elseif_bool = false;
                    esleover = false;
                    return null;
                }
                if (backIfBool)
                {

                    if (cmd.StartsWith(INCLUDE_TAG))
                    {
                        if (IncludeCommand(cmd))
                        {
                            return null;
                        }
                    }
                }
                else if (cmd.StartsWith(INCLUDE_TAG) && !ifing && !backIfBool
                      && !esleflag)
                {
                    if (IncludeCommand(cmd))
                    {
                        return null;
                    }
                }

                if (cmd.StartsWith(OUT_TAG))
                {
                    isRead = false;
                    addCommand = false;
                    executeCommand = (SELECTS_TAG + " " + readBuffer.ToString());
                }

                if (isRead)
                {
                    readBuffer.Append(cmd);
                    readBuffer.Append(FLAG);
                    addCommand = false;
                }

                if (cmd.StartsWith(IN_TAG))
                {
                    readBuffer.Remove(0, readBuffer.Length - (0));
                    isRead = true;
                    return executeCommand;
                }

                if (addCommand && ifing)
                {
                    if (backIfBool && esleflag)
                    {
                        executeCommand = cmd;
                    }

                }
                else if (addCommand)
                {
                    executeCommand = cmd;
                }

                if (cmd.StartsWith(FLAG_SAVE_TAG))
                {
                    temps = CommandSplit(cmd);
                    if (temps != null && temps.Count == 2)
                    {
                        executeCommand = cmd;
                        SaveCommand(null,null);
                        return executeCommand;
                    }
                }
                else if (cmd.StartsWith(FLAG_LOAD_TAG))
                {
                    temps = CommandSplit(cmd);
                    if (temps != null && temps.Count == 2)
                    {
                        executeCommand = cmd;
                        LoadCommand(null, -1);
                        return executeCommand;
                    }
                }

                if (executeCommand != null)
                {
                    printTags = Command.GetNameTags(executeCommand, PRINT_TAG
                            + BRACKET_LEFT_TAG, BRACKET_RIGHT_TAG);
                    if (printTags != null)
                    {
                        for (IEnumerator it = printTags.GetEnumerator(); it.MoveNext(); )
                        {
                            string key = (string)it.Current;
                            object value_ren = CollectionUtils.Get(setEnvironmentList, key);
                            if (value_ren != null)
                            {
                                executeCommand = StringUtils
                                        .ReplaceMatch(
                                                executeCommand,
                                                (PRINT_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG)
                                                       , value_ren.ToString());
                            }
                            else
                            {
                                executeCommand = StringUtils
                                        .ReplaceMatch(
                                                executeCommand,
                                                (PRINT_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG)
                                                        , key);
                            }

                        }

                    }

                    if (isCache)
                    {
                        CollectionUtils.Put(scriptContext, cacheCommandName, executeCommand);
                    }
                }
            }
            catch (Exception ex)
            {
                throw new Exception(ex.Message, ex);
            }
            finally
            {
                if (!isInnerCommand)
                {
                    offsetPos++;
                }
            }

            return executeCommand;
        }

        public string GetSaveName(string name)
        {
            string newName = scriptName + '_' + name;
            newName = StringUtils.ReplaceIgnoreCase(newName, "/", "$");
            newName = StringUtils.ReplaceIgnoreCase(newName, "\\", "$");
            return newName;
        }

        public void SaveCommand(string name, Dictionary<string, string> other)
        {
            isRead = false;
            addCommand = false;
            if (name == null && temps != null && temps.Count > 0)
            {
                name = (string)temps[1];
            }
            Session session = new Session(GetSaveName(name), false);
            for (IEnumerator<KeyValuePair<object, object>> it = setEnvironmentList.GetEnumerator(); it.MoveNext(); )
            {
                KeyValuePair<object, object> entry = it.Current;
                session.Add((string)entry.Key, (string)entry.Value);
            }
            session.Add("cmd_offsetPos", MathUtils.Min(offsetPos + 1, scriptSize));
            session.Add("cmd_cacheName", cacheCommandName);
            session.Add("cmd_nowPosFlagName", nowPosFlagName);
            session.Add("cmd_flaging", flaging);
            session.Add("cmd_ifing", ifing);
            session.Add("cmd_functioning", functioning);
            session.Add("cmd_esleflag", esleflag);
            session.Add("cmd_esleover", esleover);
            session.Add("cmd_backIfBool", backIfBool);
            session.Add("cmd_isInnerCommand", isInnerCommand);
            session.Add("cmd_isRead", isRead);
            session.Add("cmd_isCall", isCall);
            session.Add("cmd_if_bool", if_bool);
            session.Add("cmd_elseif_bool", elseif_bool);
            if (other != null)
            {
                for (IEnumerator<KeyValuePair<string, string>> it = other.GetEnumerator(); it.MoveNext(); )
                {
                    KeyValuePair<string, string> entry = it.Current;
                    session.Add(entry.Key, entry.Value);
                }
            }
            session.Save();
        }


        public void LoadCommand(string name)
        {
            LoadCommand(name, -1);
        }

        public void LoadCommand(string name, int line)
        {
            LoadCommand(name, line, null);
        }

        public Dictionary<string, string> LoadCommand(string name, int line, List<string> other)
        {
            isRead = false;
            addCommand = false;
            if (name == null && temps != null && temps.Count > 0)
            {
                name = (string)temps[1];
            }
            Session session = Session.Load(GetSaveName(name));
            if (session.GetSize() > 0)
            {
                CollectionUtils.PutAll(setEnvironmentList, session.GetRecords(0));
                int offsetLine = session.GetInt("cmd_offsetPos", offsetPos);
                if (offsetLine == offsetPos)
                {
                    GotoIndex(offsetPos + 1);
                }
                else
                {
                    GotoIndex(offsetLine);
                }
                cacheCommandName = session.Get("cmd_cacheName");
                nowPosFlagName = session.Get("cmd_nowPosFlagName");
                flaging = session.GetBoolean("cmd_flaging");
                ifing = session.GetBoolean("cmd_ifing");
                functioning = session.GetBoolean("cmd_functioning");
                esleflag = session.GetBoolean("cmd_esleflag");
                esleover = session.GetBoolean("cmd_esleover");
                backIfBool = session.GetBoolean("cmd_backIfBool");
                isInnerCommand = session.GetBoolean("cmd_isInnerCommand");
                isRead = session.GetBoolean("cmd_isRead");
                isCall = session.GetBoolean("cmd_isCall");
                if_bool = session.GetBoolean("cmd_if_bool");
                elseif_bool = session.GetBoolean("cmd_elseif_bool");
                if (other == null)
                {
                    return null;
                }
                else
                {
                    int size = other.Count;
                    Dictionary<string, string> result = new Dictionary<string, string>(size);
                    for (int i = 0; i < size; i++)
                    {
                        string otherName = other[i];
                        CollectionUtils.Put(result, otherName, session.Get(otherName));
                    }
                    return result;
                }
            }
            return null;
        }

        private bool IncludeCommand(string cmd)
        {
            temps = CommandSplit(cmd);
            StringBuilder sbr = new StringBuilder();
            for (int i = 1; i < temps.Count; i++)
            {
                sbr.Append(temps[i]);
            }
            string fileName = sbr.ToString();
            if (fileName.Length > 0)
            {
                innerCommand = new Command(fileName);
                isInnerCommand = true;
                return true;
            }
            return false;
        }

        public static string[] IncludeFile(string fileName)
        {
            if (scriptLazy == null)
            {
                scriptLazy = new Dictionary<object, object>(100);
            }
            else if (scriptLazy.Count > 10000)
            {
                scriptLazy.Clear();
            }
            int capacity = 2000;
            string key = fileName.Trim().ToLower();
            string[] result = (string[])CollectionUtils.Get(scriptLazy, key);
            if (result == null)
            {
                Stream ins = null;
                TextReader reader = null;
                result = new string[capacity];
                int length = capacity;
                int index = 0;
                try
                {
                    ins = Resources.OpenStream(fileName);
                    reader = new StreamReader(ins, System.Text.Encoding.UTF8);
                    string record = null;
                    for (; (record = reader.ReadLine()) != null; )
                    {
                        record = record.Trim();
                        if (record.Length > 0 && !record.StartsWith(FLAG_L_TAG)
                                && !record.StartsWith(FLAG_C_TAG)
                                && !record.StartsWith(FLAG_I_TAG))
                        {
                            if (index >= length)
                            {
                                result = (string[])CollectionUtils.Expand(result,
                                        capacity);
                                length += capacity;
                            }
                            result[index] = record;
                            index++;
                        }
                    }
                    result = CollectionUtils.CopyOf(result, index);
                }
                catch (Exception ex)
                {
                    throw new Exception(ex.Message, ex);
                }
                finally
                {
                    try
                    {
                        ins.Close();
                        ins = null;
                    }
                    catch (IOException e)
                    {
                        Log.Exception(e);
                    }
                    if (reader != null)
                    {
                        try
                        {
                            reader.Close();
                            reader = null;
                        }
                        catch (IOException e)
                        {
                            Log.Exception(e);
                        }
                    }
                }
                CollectionUtils.Put(scriptLazy, key, result);
                return result;
            }
            else
            {
                return CollectionUtils.CopyOf(result);
            }

        }

        public static string[] IncludeFile(Stream ins)
        {
            if (scriptLazy == null)
            {
                scriptLazy = new Dictionary<object, object>(100);
            }
            else if (scriptLazy.Count > 10000)
            {
                scriptLazy.Clear();
            }
            int capacity = 2000;
            string key = ins.GetHashCode().ToString();
            string[] result = (string[])CollectionUtils.Get(scriptLazy, key);
            if (result == null)
            {
                TextReader reader = null;
                result = new string[capacity];
                int length = capacity;
                int index = 0;
                try
                {
                    reader = new StreamReader(ins, System.Text.Encoding.UTF8);
                    string record = null;
                    for (; (record = reader.ReadLine()) != null; )
                    {
                        record = record.Trim();
                        if (record.Length > 0 && !record.StartsWith(FLAG_L_TAG)
                                && !record.StartsWith(FLAG_C_TAG)
                                && !record.StartsWith(FLAG_I_TAG))
                        {
                            if (index >= length)
                            {
                                result = (string[])CollectionUtils.Expand(result,
                                        capacity);
                                length += capacity;
                            }
                            result[index] = record;
                            index++;
                        }
                    }
                    result = CollectionUtils.CopyOf(result, index);
                }
                catch (Exception ex)
                {
                    throw new Exception(ex.Message, ex);
                }
                finally
                {
                    try
                    {
                        ins.Close();
                        ins = null;
                    }
                    catch (IOException e)
                    {
                        Log.Exception(e);
                    }
                    if (reader != null)
                    {
                        try
                        {
                            reader.Close();
                            reader = null;
                        }
                        catch (IOException e)
                        {
                            Log.Exception(e);
                        }
                    }
                }
                CollectionUtils.Put(scriptLazy, key, result);
                return result;

            }
            else
            {
                return CollectionUtils.CopyOf(result);
            }
        }

        public static IList CommandSplit(string src)
        {
            string result = UpdateOperator(src);
            string[] cmds = StringUtils.Split(result, FLAG);
            return Loon.Java.Collections.Arrays.AsList(cmds);
        }

        public static void ReleaseCache()
        {
            if (setEnvironmentList != null)
            {
                setEnvironmentList.Clear();
                setEnvironmentList = null;
            }
            if (conditionEnvironmentList != null)
            {
                conditionEnvironmentList.Clear();
                conditionEnvironmentList = null;
            }
            if (functions != null)
            {
                functions.Clear();
                functions = null;
            }
            if (scriptContext != null)
            {
                scriptContext.Clear();
                scriptContext = null;
            }
            if (scriptLazy != null)
            {
                scriptLazy.Clear();
                scriptLazy = null;
            }
        }

        public bool IsClose()
        {
            return isClose;
        }

        public void Dispose()
        {
            this.isClose = true;
            if (readBuffer != null)
            {
                readBuffer = null;
            }
            if (temps != null)
            {
                try
                {
                    CollectionUtils.Clear(temps);
                    temps = null;
                }
                catch (Exception)
                {
                }
            }
            if (printTags != null)
            {
                CollectionUtils.Clear(printTags);
                printTags = null;
            }
            if (randTags != null)
            {
                CollectionUtils.Clear(randTags);
                randTags = null;
            }
            if (exp != null)
            {
                exp.Dispose();
            }
        }
    }
}
