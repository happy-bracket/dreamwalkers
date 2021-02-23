require"scripting_utils/components"

function init(invoker)
    local level = invoker:getInteractor():loadLevel("observatory")
    local spawn_point = invoker:getRegistry():get("spawn_point")
    local xy = position_mapper:get(spawn_point):getXy()

    local luna_comp = luajava.newInstance(luna_component)
    local aerial_comp = luajava.newInstance(aerial_component)
    local look_comp = luajava.newInstance(look_component)

    local luna = invoker:getSpawner():spawn(1.8, 1.7, xy, 7.5)
    luna:add(luna_comp)
    luna:add(aerial_comp)
    luna:add(look_comp)
end
