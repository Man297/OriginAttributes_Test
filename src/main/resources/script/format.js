function main(context, argument) {
    var amount = parseInt(argument.get(0))
    var string = ""
    for(var i = 0;i < 20; i++) {
        var char = amount > i ? 'b' : '7'
        string += '&' + char + "|"
    }

    return string
}