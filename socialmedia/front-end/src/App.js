import React, { useEffect, useState } from "react";
import {BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useUser } from './UserProvider/UserProvider';

import Login from './Auth/Login';
import PrivateRoute from "./Auth/PrivateRoute";

import Dashboard from "./Dashboard/Dashboard";
import Friends from "./Friends/Friends";
import FriendsRequests from "./Friends/FriendsRequests";
import FriendsAdd from "./Friends/FriendsAdd";
import FriendsAll from "./Friends/FriendsAll";
import MyPosts from "./MyPosts/MyPosts";
import Register from "./Auth/Register";
import EditPersonalInfo from "./UserInfo/EditPersonalInfo";
import EditPost from "./Components/MyPosts/Posts/EditPost";
import DeletePost from "./Components/MyPosts/Posts/DeletePost";
import Pages from "./Pages/Pages";
import PagesAdd from "./Pages/PagesAdd";
// import PageDashboard from "./Dashboard/PageDashboard";
import MyPages from "./Pages/MyPages";
import Page from "./Pages/Page";
import EditPagePosts from "./Components/Pages/EditPosts"
import UserViewPage from "./Pages/UserViewOn/Page";
import ViewMyPosts from "./MyPosts/ViewMyPosts";
import PageAllImages from "./Components/Pages/PageAllImages";
import PageAllVideos from "./Components/Pages/PageAllVideos";
import ProfileAllImages from "./Components/MyPosts/Media/ProfileAllImages";
import ViewProfileAllImages from "./Components/MyPosts/Media/ViewProfileAllImages";
import ViewAllFollowPages from "./Components/MyPosts/Follow/ViewAllFollowPages";
import ViewAllFriendsVisitUser from "./Components/MyPosts/Friends/ViewAllFriendsVisitUser";
import MSE from "./test/MSE";


function App() {
  const user = useUser();
  const [roles, setRoles] = useState([]);

  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login/>} />
        <Route path="/register" element={<Register />} />
        {/* This section is for dashboard */}
        <Route path="/dashboard" exact element={ <PrivateRoute><Dashboard/></PrivateRoute>} />
        {/* <Route path="/pageDashboard" element={<PageDashboard/>} />  */}

        {/* This section is for myPosts */}
        <Route path="/users/:userId/myPosts" element={<PrivateRoute><MyPosts /></PrivateRoute>} />
        <Route path="/users/:userId/myPosts/myInfo/:personalInfoId/Edit" element={ <PrivateRoute><EditPersonalInfo /></PrivateRoute> } />
        <Route path="/users/:userId/myPosts/:postId/edit" element={<PrivateRoute><EditPost /></PrivateRoute>} />
        <Route path="/users/:userId/myPosts/allImg" element={<PrivateRoute><ProfileAllImages/></PrivateRoute>}/>
        {/*Note. might have to delete the post from the page myPosts */}
        <Route path="/users/:userId/myPosts/:postId/delete" element={<PrivateRoute><DeletePost /></PrivateRoute>} /> 

        {/*This section is for pages */}
        <Route path="/users/:userId/pages/mypages" element={<PrivateRoute><MyPages/></PrivateRoute>} />
        <Route path="/users/:userId/pages/addPages" element={<PrivateRoute><PagesAdd/></PrivateRoute>} />
        <Route path="/users/:userId/pages" element={<PrivateRoute><Pages /></PrivateRoute>} />
        <Route path="users/:userId/pages/page/:pageId" element={<PrivateRoute><Page/></PrivateRoute>} />
        <Route path="users/:userId/pages/page/:pageId/posts/post/:postId" element={<PrivateRoute><EditPagePosts /></PrivateRoute>} />

        {/* This section is for friends */}
        <Route path="/users/:userId/friends" element={<PrivateRoute> <Friends/></PrivateRoute>} />
        <Route path="/users/:userId/friends/requests" element={<PrivateRoute> <FriendsRequests/></PrivateRoute>} />
        <Route path="/users/:userId/friends/addFriends" element={<PrivateRoute> <FriendsAdd/></PrivateRoute>} />
        <Route path="/users/:userId/friends/allFriends" element={<PrivateRoute> <FriendsAll/></PrivateRoute>} />

        {/* This section is for user visit to different path */}
        <Route path="/users/:userId/user/:visitUserId/profile" element={<PrivateRoute><ViewMyPosts/></PrivateRoute>} />
        <Route path="/users/:userId/pages/page/:pageId/profile/allImg" element={<PrivateRoute><PageAllImages/></PrivateRoute>}/>
        <Route path="/users/:userId/pages/page/:pageId/profile/allVid" element={<PrivateRoute><PageAllVideos/></PrivateRoute>}/>
        <Route path="/users/:userId/user/:visitUserId/profile/allImg" element={<PrivateRoute><ViewProfileAllImages/></PrivateRoute>} />
        <Route path="/users/:userId/user/:visitUserId/profile/allPages" element={<PrivateRoute><ViewAllFollowPages /></PrivateRoute>} />
        <Route path="/users/:userId/user/:visitUserId/profile/allFriends" element={<PrivateRoute><ViewAllFriendsVisitUser /></PrivateRoute>} />

        <Route path="/mse" element={<MSE/>} />
      </Routes>
    </Router>
  );
}

export default App;
