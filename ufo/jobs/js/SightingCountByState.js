var map = function(key, value, context) {
    var record;
    try {
        record = JSON.parse(value);
    }
    catch (e) {
        return;
    }

    var state = record.location.substring(record.location.length - 2);
    if (state[0] >= 'A' && state[0] <= 'Z' && state[1] >= 'A' && state[1] <= 'Z') {
        context.write(state, 1);
    }
}

var reduce = function(key, values, context) {
    var state = key;
    var sightingCount = 0;

    while (values.hasNext()) {
        sightingCount++;
        values.next();
    }

    context.write(state, sightingCount);
}
