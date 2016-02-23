$(document).ready(function () {

    $('#sorting_field').addClass('hidden');

    var default_group = $('#group').find(':selected');

    var organisation = $('#organisation').find(':selected');
    if(organisation.val() != null){
        updateGroups(organisation.val())
    }

    $('#organisation').change(function () {
        var id = $('#organisation').find(':selected').val();
        var name = $('#organisation').find(':selected').text();
        updateGroups(id);
        updateSorting(id, name)
    });

    function updateGroups(organisation) {
        $('#group').empty();
        if (organisation.length > 0) {
            $.getJSON(
                '/api/organisations/groups/' + organisation,
                function (groups) {
                    $.each(groups, function (id, group) {
                        $('#group').append($('<option>').text(group).attr('value', id))
                    })
                }
            );
            $('#group').append(default_group.text("Selecteer een groep"))
        } else {
            $('#group').append(default_group)
        }
    }

    function updateSorting(id, name) {
        if (name == "Scouting Kapelle" || id.length == 0) {
            $('#sorting_field').addClass('hidden')
        } else {
            $('#sorting_field').removeClass('hidden')
        }
    }
});