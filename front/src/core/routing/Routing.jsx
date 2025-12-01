import {createBrowserRouter} from "react-router-dom";

import AdminFuncPages from "../../ui/pages/AdminFuncPages.jsx";
import EmployeePages from "../../ui/pages/EmployeePages.jsx";
import UserInfoPages from "../../ui/pages/UserInfoPages.jsx";
import UserFuncPages from "../../ui/pages/UserFuncPages.jsx";

const routes = [
    {
        path: "/",
        element: <UserInfoPages />,
    },
    {
        path: "/admin",
        element: <AdminFuncPages />,
    },
    {
        path: "/employee",
        element: <EmployeePages />,
    },
    {
        path: "/userFunc",
        element: <UserFuncPages />,
    }
]

const routing = createBrowserRouter(routes)
export default routing