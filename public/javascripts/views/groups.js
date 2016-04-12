$(document).ready(function () {

    $('#groups').on('click-row.bs.table', function (e, row) {
        window.location.href = '/groups/' + row[0]
    });

    $('#group').on('click-row.bs.table', function (e, row) {
        window.location.href = '/registrations/' + row[0]
    });

    $('#categories').on('click-row.bs.table', function (e, row) {
        window.location.href = '/categories/' + row[0]
    })
});