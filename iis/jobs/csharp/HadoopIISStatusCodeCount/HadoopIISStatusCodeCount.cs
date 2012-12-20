using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text.RegularExpressions;
using Microsoft.Hadoop.MapReduce;

namespace HadoopIISStatusCodeCount
{
    public class HadoopIISStatusCodeCountMap : MapperBase
    {
        private const int ExpectedLogEntryComponentCount = 15;
        private const int HttpStatusCodeComponentIndex = 9;

        public override void Map(string inputLine, MapperContext context)
        {
            var splitEntry = Regex.Split(inputLine, @"\s+");
            if (splitEntry.Length != ExpectedLogEntryComponentCount)
            {
                return;
            }

            var statusCode = splitEntry[HttpStatusCodeComponentIndex];
            string key = "HTTP " + statusCode;
            string value = "1";
            
            context.EmitKeyValue(key, value);
        }
    }

    public class HadoopIISStatusCodeCountReduce : ReducerCombinerBase
    {
        public override void Reduce(string key, IEnumerable<string> values, ReducerCombinerContext context)
        {
            context.EmitKeyValue
            (
                key,
                values.Sum(v => int.Parse(v)).ToString(CultureInfo.InvariantCulture)
            );
        }
    }

    public class HadoopIISStatusCodeCountJob : HadoopJob<HadoopIISStatusCodeCountMap, HadoopIISStatusCodeCountReduce>
    {
        public override HadoopJobConfiguration Configure(ExecutorContext context)
        {
            var config = new HadoopJobConfiguration
            {
                InputPath = context.Arguments[0],
                OutputFolder = context.Arguments[1]
            };

            return config;
        }
    }
}
