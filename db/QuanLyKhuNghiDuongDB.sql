USE [master]
GO
/****** Object:  Database [QuanLyKhuNghiDuong]    Script Date: 6/25/2025 6:33:33 AM ******/
CREATE DATABASE [QuanLyKhuNghiDuong]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'QuanLyKhuNghiDuong', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\QuanLyKhuNghiDuong.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'QuanLyKhuNghiDuong_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\QuanLyKhuNghiDuong_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [QuanLyKhuNghiDuong].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET ARITHABORT OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET  DISABLE_BROKER 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET  MULTI_USER 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET DB_CHAINING OFF 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET QUERY_STORE = ON
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [QuanLyKhuNghiDuong]
GO
/****** Object:  Table [dbo].[Bookings]    Script Date: 6/25/2025 6:33:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Bookings](
	[booking_id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NOT NULL,
	[room_id] [int] NOT NULL,
	[check_in_date] [date] NOT NULL,
	[check_out_date] [date] NOT NULL,
	[total_price] [decimal](18, 2) NOT NULL,
	[status] [nvarchar](20) NOT NULL,
	[created_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[booking_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Feedback]    Script Date: 6/25/2025 6:33:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Feedback](
	[feedback_id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NOT NULL,
	[content] [nvarchar](max) NOT NULL,
	[created_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[feedback_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Notifications]    Script Date: 6/25/2025 6:33:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Notifications](
	[notification_id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NOT NULL,
	[title] [nvarchar](255) NOT NULL,
	[message] [nvarchar](max) NULL,
	[sent_at] [datetime] NULL,
	[is_read] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[notification_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Resorts]    Script Date: 6/25/2025 6:33:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Resorts](
	[resort_id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](100) NOT NULL,
	[location] [nvarchar](255) NOT NULL,
	[type] [nvarchar](50) NULL,
	[description] [nvarchar](max) NULL,
	[price_range] [nvarchar](50) NULL,
	[amenities] [nvarchar](max) NULL,
	[created_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[resort_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Room_Status_History]    Script Date: 6/25/2025 6:33:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Room_Status_History](
	[status_id] [int] IDENTITY(1,1) NOT NULL,
	[room_id] [int] NOT NULL,
	[status] [nvarchar](20) NOT NULL,
	[updated_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[status_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Rooms]    Script Date: 6/25/2025 6:33:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Rooms](
	[room_id] [int] IDENTITY(1,1) NOT NULL,
	[resort_id] [int] NOT NULL,
	[room_number] [nvarchar](50) NOT NULL,
	[room_type] [nvarchar](50) NULL,
	[price_per_night] [decimal](18, 2) NOT NULL,
	[status] [nvarchar](20) NOT NULL,
	[capacity] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[room_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Service_Bookings]    Script Date: 6/25/2025 6:33:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Service_Bookings](
	[service_booking_id] [int] IDENTITY(1,1) NOT NULL,
	[booking_id] [int] NOT NULL,
	[service_id] [int] NOT NULL,
	[quantity] [int] NOT NULL,
	[total_price] [decimal](18, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[service_booking_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Services]    Script Date: 6/25/2025 6:33:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Services](
	[service_id] [int] IDENTITY(1,1) NOT NULL,
	[resort_id] [int] NOT NULL,
	[name] [nvarchar](100) NOT NULL,
	[description] [nvarchar](max) NULL,
	[price] [decimal](18, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[service_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Users]    Script Date: 6/25/2025 6:33:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[user_id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](100) NOT NULL,
	[email] [nvarchar](100) NOT NULL,
	[password_hash] [nvarchar](255) NULL,
	[phone] [nvarchar](20) NULL,
	[role] [nvarchar](20) NOT NULL,
	[created_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Users_Username] UNIQUE NONCLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Bookings] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[Feedback] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[Notifications] ADD  DEFAULT (getdate()) FOR [sent_at]
GO
ALTER TABLE [dbo].[Notifications] ADD  DEFAULT ((0)) FOR [is_read]
GO
ALTER TABLE [dbo].[Resorts] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[Room_Status_History] ADD  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[Users] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[Bookings]  WITH CHECK ADD  CONSTRAINT [FK_Bookings_Rooms] FOREIGN KEY([room_id])
REFERENCES [dbo].[Rooms] ([room_id])
GO
ALTER TABLE [dbo].[Bookings] CHECK CONSTRAINT [FK_Bookings_Rooms]
GO
ALTER TABLE [dbo].[Bookings]  WITH CHECK ADD  CONSTRAINT [FK_Bookings_Users] FOREIGN KEY([user_id])
REFERENCES [dbo].[Users] ([user_id])
GO
ALTER TABLE [dbo].[Bookings] CHECK CONSTRAINT [FK_Bookings_Users]
GO
ALTER TABLE [dbo].[Feedback]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[Users] ([user_id])
GO
ALTER TABLE [dbo].[Notifications]  WITH CHECK ADD  CONSTRAINT [FK_Notifications_Users] FOREIGN KEY([user_id])
REFERENCES [dbo].[Users] ([user_id])
GO
ALTER TABLE [dbo].[Notifications] CHECK CONSTRAINT [FK_Notifications_Users]
GO
ALTER TABLE [dbo].[Room_Status_History]  WITH CHECK ADD  CONSTRAINT [FK_RoomStatusHistory_Rooms] FOREIGN KEY([room_id])
REFERENCES [dbo].[Rooms] ([room_id])
GO
ALTER TABLE [dbo].[Room_Status_History] CHECK CONSTRAINT [FK_RoomStatusHistory_Rooms]
GO
ALTER TABLE [dbo].[Rooms]  WITH CHECK ADD  CONSTRAINT [FK_Rooms_Resorts] FOREIGN KEY([resort_id])
REFERENCES [dbo].[Resorts] ([resort_id])
GO
ALTER TABLE [dbo].[Rooms] CHECK CONSTRAINT [FK_Rooms_Resorts]
GO
ALTER TABLE [dbo].[Service_Bookings]  WITH CHECK ADD  CONSTRAINT [FK_ServiceBookings_Bookings] FOREIGN KEY([booking_id])
REFERENCES [dbo].[Bookings] ([booking_id])
GO
ALTER TABLE [dbo].[Service_Bookings] CHECK CONSTRAINT [FK_ServiceBookings_Bookings]
GO
ALTER TABLE [dbo].[Service_Bookings]  WITH CHECK ADD  CONSTRAINT [FK_ServiceBookings_Services] FOREIGN KEY([service_id])
REFERENCES [dbo].[Services] ([service_id])
GO
ALTER TABLE [dbo].[Service_Bookings] CHECK CONSTRAINT [FK_ServiceBookings_Services]
GO
ALTER TABLE [dbo].[Services]  WITH CHECK ADD  CONSTRAINT [FK_Services_Resorts] FOREIGN KEY([resort_id])
REFERENCES [dbo].[Resorts] ([resort_id])
GO
ALTER TABLE [dbo].[Services] CHECK CONSTRAINT [FK_Services_Resorts]
GO
ALTER TABLE [dbo].[Bookings]  WITH CHECK ADD  CONSTRAINT [CK_Bookings_status_new] CHECK  (([status]='cancelled' OR [status]='checkedin' OR [status]='pending'))
GO
ALTER TABLE [dbo].[Bookings] CHECK CONSTRAINT [CK_Bookings_status_new]
GO
ALTER TABLE [dbo].[Room_Status_History]  WITH CHECK ADD CHECK  (([status]='maintenance' OR [status]='booked' OR [status]='available'))
GO
ALTER TABLE [dbo].[Rooms]  WITH CHECK ADD CHECK  (([status]='maintenance' OR [status]='booked' OR [status]='available'))
GO
ALTER TABLE [dbo].[Users]  WITH CHECK ADD CHECK  (([role]='customer' OR [role]='admin'))
GO
USE [master]
GO
ALTER DATABASE [QuanLyKhuNghiDuong] SET  READ_WRITE 
GO
