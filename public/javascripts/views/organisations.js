$(document).ready(function () {

    $('#organisations').on('click-row.bs.table', function (e, row) {
        window.location.href = '/organisations/' + row[0]
    })
});
